package com.glodblock.github.glodium.reflect.auto;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.reflect.ReflectKit;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraftforge.api.distmarker.Dist;
import org.slf4j.Logger;

import java.util.Arrays;

public class AutoReflects {

    public static void process(Class<?> clazz, Logger logger) {
        processInternal(clazz, null, logger);
    }

    public static void process(Object holder, Logger logger) {
        processInternal(holder.getClass(), holder, logger);
    }

    private static void processInternal(Class<?> clazz, Object obj, Logger logger) {
        if (clazz.isAnnotationPresent(ReflectHost.class)) {
            boolean silent = clazz.getAnnotation(ReflectHost.class).silent();
            var fields = clazz.getDeclaredFields();
            for (var rf : fields) {
                if (rf.isAnnotationPresent(AutoReflect.class)) {
                    var property = rf.getAnnotation(AutoReflect.class);
                    if (!checkSide(property.side())) {
                        continue;
                    }
                    try {
                        rf.setAccessible(true);
                        Class<?> host = getClazz(property.host(), property.path());
                        if (rf.getType() == FieldObj.class) {
                            var field = new FieldObj(ReflectKit.reflectField(host, property.name()));
                            ReflectKit.writeField(obj, rf, field);
                        }
                        if (rf.getType() == MethodObj.class) {
                            var field = new MethodObj(ReflectKit.reflectMethod(host, property.name(), property.paras()));
                            ReflectKit.writeField(obj, rf, field);
                        }
                        if (rf.getType() == ConObj.class) {
                            var field = new ConObj(ReflectKit.reflectCon(host, property.paras()));
                            ReflectKit.writeField(obj, rf, field);
                        }
                    } catch (Throwable e) {
                        if (!silent) {
                            logger.error("Fail to init reflection in {}. Properties[host={}, path={}, names={}, paras={}, side={}]",
                                    clazz.getName(), property.host(), property.path(), property.name(), property.paras(), property.side());
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        } else {
            Glodium.LOGGER.warn("{} is missing reflection annotation.", clazz.getName());
        }
    }

    private static boolean checkSide(AutoReflect.Environment isClient) {
        return switch (isClient) {
            case ALL -> true;
            case SERVER -> GlodUtil.side() == Dist.DEDICATED_SERVER;
            case CLIENT -> GlodUtil.side() == Dist.CLIENT;
        };
    }

    private static Class<?> getClazz(Class<?> clazz, String[] alt) throws ClassNotFoundException {
        if (clazz != AutoReflect.NAC.class) {
            return clazz;
        }
        for (var path : alt) {
            try {
                return Class.forName(path);
            } catch (ClassNotFoundException ignored) {
                // NO-OP
            }
        }
        throw new ClassNotFoundException(Arrays.toString(alt));
    }

}
