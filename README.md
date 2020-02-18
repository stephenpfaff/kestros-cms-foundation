# Kestros CMS Foundation
Foundational logic for the Kestros CMS. Contains all the models, scripts, and baseline templates/components required for rendering a site.

- [Getting Started](#getting-started)
- [UI Frameworks](#ui-frameworks)
  * [Creating a new UiFramework](#creating-a-new-uiframework)
  * [Configure](#configure)
  * [Vendor Libraries](#vendor-libraries)
    + [Configure](#configure-1)
  * [Default Theme](#default-theme)
  * [Themes](#themes)
    + [Configure](#configure-2)
  * [CSS/JS Compile Order](#css-js-compile-order)
  * [Compiled HTL Templates](#compiled-htl-templates)
  * [Component UiFramework Views](#component-uiframework-views)
- [Component Types](#component-types)
  * [Creating a new ComponentType](#creating-a-new-componenttype)
    + [Extending KestrosParent Component](#extending-kestrosparent-component)
    + [Using HTL Templates from a UiLibrary](#using-htl-templates-from-a-uilibrary)
  * [Common View](#common-view)
  * [ComponentUiFrameworkViews](#componentuiframeworkviews)
  * [Validation](#validation)
- [Content](#content)
- [Validate](#validate)

## Getting Started

## UI Frameworks

UI Frameworks provide CSS and JS libraries to a site by compiling from reusable `VendorLibraries`, one-off `ComponentUiFrameworkView` scripts, and and its own contained scripts.

### Creating a new UiFramework

Under `/etc/ui-frameworks`, create a new resource for your UiFramework and add the following `.content.xml`.
  
```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
  xmlns:kes="http://kestros.slingware.com/kes/1.0"
  jcr:primaryType="kes:UiFramework"
  jcr:title="My UI Framework"
  jcr:description
  kes:uiFrameworkCode=""
  kes:vendorLibraries="[my-vendor-library-1,my-vendor-library-2]"/>
```

### Configure

| Property  | Description | Default Value | 
|-----------|-------------|---------------|
| jcr:title | Framework title| EMPTY |
|kes:uiFrameworkCode | |     `common` |
|kes:vendorLibraries | | `[ ]` |

### Vendor Libraries

```

```

#### Configure


| Property  | Description | Default Value | 
|-----------|-------------|---------------|
| jcr:title | Framework title| EMPTY |

### Default Theme

### Themes
#### Configure
### CSS/JS Compile Order
### Compiled HTL Templates
### Component UiFramework Views

## Component Types
### Creating a new ComponentType
#### Extending KestrosParent Component
#### Using HTL Templates from a UiLibrary
### Common View
### ComponentUiFrameworkViews
### Validation

## Content

## Validate