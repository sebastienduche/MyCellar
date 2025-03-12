package mycellar.myoptions;

import mycellar.general.IResource;

public record MyOptionKey(IResource resource, String defaultValue, String propertyKey, MyOptionObjectType objectType) {
}
