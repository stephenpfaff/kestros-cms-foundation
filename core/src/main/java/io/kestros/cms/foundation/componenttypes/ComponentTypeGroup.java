package io.kestros.cms.foundation.componenttypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;

public class ComponentTypeGroup {

  private String title;
  private final List<ComponentType> componentTypes = new ArrayList<>();

  public ComponentTypeGroup() {
    this.title = "No Group";
  }

  public void setTitle(@Nonnull final String title) {
    this.title = title;
  }

  @Nonnull
  public String getTitle() {
    return title;
  }

  public void addComponentType(@Nonnull final ComponentType componentType) {
    this.componentTypes.add(componentType);
  }

  /**
   * Removes the specified ComponentType from the current ComponentTypeGroup.
   *
   * @param path ComponentType to remove.
   */
  public void removeComponentType(@Nonnull final String path) {
    this.componentTypes.removeIf(componentType -> path.equals(componentType.getPath()));
  }

  public void removeComponentType(@Nonnull final ComponentType componentType) {
    this.removeComponentType(componentType.getPath());
  }

  @Nonnull
  public List<ComponentType> getComponentTypes() {
    componentTypes.sort(new ComponentTypeSorter());
    return componentTypes;
  }

  private static class ComponentTypeSorter implements Comparator<ComponentType>, Serializable {

    private static final long serialVersionUID = -365044729256904870L;

    @Override
    public int compare(final ComponentType o1, final ComponentType o2) {
      return o1.getTitle().compareTo(o2.getTitle());
    }
  }
}
