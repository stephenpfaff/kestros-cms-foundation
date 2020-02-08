package io.kestros.cms.foundation.componenttypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Titled list of ComponentTypes with the same 'componentGroup' property.
 */
public class ComponentTypeGroup {

  private String title;
  private final List<ComponentType> componentTypes = new ArrayList<>();

  /**
   * Creates a new ComponentGroup and sets the default title to 'No Group'.
   */
  public ComponentTypeGroup() {
    this.title = "No Group";
  }

  /**
   * Sets group title.
   *
   * @param title Title.
   */
  public void setTitle(@Nonnull final String title) {
    this.title = title;
  }

  /**
   * Title / Display name of the group.
   *
   * @return Title / Display name of the group.
   */
  @Nonnull
  public String getTitle() {
    return title;
  }

  /**
   * Adds a ComponentType to the group.
   *
   * @param componentType ComponentType to add.
   */
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

  /**
   * Removes a ComponentType from the ComponentTypeGroup.
   *
   * @param componentType ComponentType to remove.
   */
  public void removeComponentType(@Nonnull final ComponentType componentType) {
    this.removeComponentType(componentType.getPath());
  }

  /**
   * ComponentsTypes that belong to the current ComponentTypeGroup.
   *
   * @return ComponentsTypes that belong to the current ComponentTypeGroup.
   */
  @Nonnull
  public List<ComponentType> getComponentTypes() {
    componentTypes.sort(new ComponentTypeSorter());
    return componentTypes;
  }

  /**
   * Sorts ComponentTypes within a ComponentTypeGroup by Title (alphabetically).
   */
  private static class ComponentTypeSorter implements Comparator<ComponentType>, Serializable {

    private static final long serialVersionUID = -365044729256904870L;

    @Override
    public int compare(final ComponentType o1, final ComponentType o2) {
      return o1.getTitle().compareTo(o2.getTitle());
    }
  }
}
