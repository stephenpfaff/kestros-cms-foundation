package io.kestros.cms.foundation.design.htltemplate;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;

public class HtlTemplateParameter {

  private String name;
  private String description;
  private String classPath;
  private Node node;

  HtlTemplateParameter(String name, Node node) {
    this.name = name;
    this.node = node;
    this.description = "";
    this.classPath = "";
    final Attributes attributes = this.node.attributes();
    for (Attribute attribute : attributes) {
      if (attribute.getKey().toUpperCase().equals(
          ("data-" + name + "-description").toUpperCase())) {
        this.description = attribute.getValue();
      }
      if (attribute.getKey().toUpperCase().equals(("data-" + name + "-class").toUpperCase())) {
        this.classPath = attribute.getValue();
      }
    }

  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public String getExpectedClasspath() {
    return this.classPath;
  }

}
