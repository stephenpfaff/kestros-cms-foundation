package io.kestros.cms.foundation.services.pagerendermethod.impl;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;

import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.services.pagecacheservice.GeneralPageCacheService;
import io.kestros.cms.foundation.services.pagecacheservice.PageCacheService;
import io.kestros.cms.foundation.services.pagerendermethod.PageRenderMethod;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline strategy for rendering content pages in Kestros.
 */
@Component(immediate = true,
           service = PageRenderMethod.class,
           property = "service.ranking:Integer=1")
public class BasePageRenderMethod implements PageRenderMethod {

  private static final Logger LOG = LoggerFactory.getLogger(BasePageRenderMethod.class);

  @Reference
  private GeneralPageCacheService pageCacheService;

  @Override
  public void doRender(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response) throws IOException {
    final Resource resource = request.getResource();

    final BaseContentPage page = resource.adaptTo(BaseContentPage.class);
    if (page != null) {
      try {
        if (getPageCacheService() != null) {
          String pageOutput = getPageCacheService().getCachedOutput(page);
          pageOutput = pageOutput.replaceAll("\\s+", " ");
          response.setContentType("text/html");
          response.getWriter().write(pageOutput);
          return;
        }
      } catch (final CacheRetrievalException e) {
        LOG.warn("Failed to retrieve cached page {}. {}", page.getPath(), e.getMessage());
      }
      try {
        final BaseResource jcrContentResource = getChildAsType(JCR_CONTENT, resource,
            BaseResource.class);
        final RequestDispatcher requestDispatcher = request.getRequestDispatcher(
            jcrContentResource.getResource());

        if (requestDispatcher != null) {
          response.setContentType("text/html");

          final PageResponseWrapper wrapper = new PageResponseWrapper(response);
          requestDispatcher.include(request, wrapper);

          final CharArrayWriter writer = new CharArrayWriter();
          final String originalContent = wrapper.getResponseContent();

          writer.write(originalContent);
          response.setContentLength(writer.toString().length());

          final PrintWriter out = response.getWriter();

          out.write(wrapper.getResponseContent());
          out.close();
          getPageCacheService().cachePage(page, wrapper.getResponseContent());
        } else {
          LOG.error("Failed to get request dispatcher for content of {}", request.getResource());
          throw new ServletException("Unable to render. No content found.");
        }

      } catch (final ModelAdaptionException | ServletException | IOException exception) {
        LOG.error("Unable to render page {} due to invalid or missing jcr:content resource",
            request.getResource());
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "No content found.");
      } catch (final CacheBuilderException e) {
        LOG.warn("Failed to build cache for page {}. {}", page.getPath(), e.getMessage());
      }
    }
  }

  protected PageCacheService getPageCacheService() {
    return pageCacheService;
  }

  @Override
  public Boolean useRenderMethod(final SlingHttpServletRequest request) {
    return true;
  }

  /**
   * Response wrapper used for storing HTML output, for caching pages.
   */
  public static class PageResponseWrapper extends HttpServletResponseWrapper {

    private final CharArrayWriter output;

    /**
     * Constructs the PageResponseWrapper.
     *
     * @param response Response to write to.
     */
    public PageResponseWrapper(final HttpServletResponse response) {
      super(response);
      output = new CharArrayWriter();
    }

    /**
     * Response content.
     *
     * @return Response content.
     */
    public String getResponseContent() {
      return output.toString();
    }

    /**
     * Response writer.
     *
     * @return Response writer.
     */
    public PrintWriter getWriter() {
      return new PrintWriter(output);
    }
  }
}
