# Kestros CMS Foundation

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
  jcr:title=""
  kes:uiFrameworkCode=""
  kes:vendorLibraries="[]"/>

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