package io.kestros.cms.temp.services;

public class DependencyService {


  //  /**
  //   * Vendor Libraries which contain the current Vendor Library as a dependency.
  //   *
  //   * @return Vendor Libraries which contain the current Vendor Library as a dependency.
  //   */
  //  public List<VendorLibrary> getDependencyOfList() {
  //    List<VendorLibrary> dependencyOfList = new ArrayList<>();
  //    try {
  //      for (VendorLibrary vendorLibrary : getAllVendorLibraries(getResourceResolver(), true,
  //      true)) {
  //        if (!vendorLibrary.getPath().equals(getPath())) {
  //          for (UiLibrary uiLibrary : vendorLibrary.getDependencies()) {
  //            if (uiLibrary.getPath().equals(getPath())) {
  //              dependencyOfList.add(vendorLibrary);
  //            }
  //          }
  //        }
  //      }
  //    } catch (ResourceNotFoundException e) {
  //      LOG.warn("Unable to find vendor libraries root resource. {}.", e.getMessage());
  //    }
  //    return dependencyOfList;
  //  }

  //  /**
  //   * List of UI Frameworks which reference the current Vendor Library.
  //   *
  //   * @return List of UI Frameworks which reference the current Vendor Library.
  //   */
  //  public List<UiFramework> getReferencingUiFrameworks() {
  //    List<UiFramework> referencingUiFrameworks = new ArrayList<>();
  //    for (UiFramework uiFramework : DesignUtils.getAllUiFrameworks(getResourceResolver(), true,
  //        true)) {
  //      for (VendorLibrary vendorLibrary : uiFramework.getVendorLibraries()) {
  //        if (vendorLibrary.getPath().equals(getPath())) {
  //          referencingUiFrameworks.add(uiFramework);
  //          continue;
  //        }
  //      }
  //    }
  //    return referencingUiFrameworks;
  //  }


}
