package br.opengl;

import net.bytebuddy.*;
import net.bytebuddy.agent.*;
import net.bytebuddy.asm.*;
import net.bytebuddy.dynamic.*;
import net.bytebuddy.dynamic.loading.*;
import net.bytebuddy.pool.*;
import org.lwjgl.opengl.*;

import static net.bytebuddy.matcher.ElementMatchers.*;

public abstract class OpenGL {

    private OpenGL() {}

    public static void bytebudy() {
        ByteBuddyAgent.install();

        final var loader = TypePool.Default.ofSystemLoader();
        final var classFileLoader = ClassFileLocator.ForClassLoader.ofSystemLoader();
        final var classLoader = Thread.currentThread().getContextClassLoader();
        final var strategy = ClassReloadingStrategy.fromInstalledAgent();

        final var byteBudy = new ByteBuddy();
        for (var name : new String[]{
                "GL11C", "GL12C", "GL13C", "GL14C", "GL15C",
                "GL20C", "GL21C",
                "GL30C", "GL31C", "GL32C", "GL33C",
                "GL40C", "GL41C", "GL42C", "GL43C", "GL44C", "GL45C", "GL46C"
        }) {
            final var start = System.currentTimeMillis();
            byteBudy
                    .redefine(
                            loader.describe("org.lwjgl.opengl.%s".formatted(name)).resolve(),
                            classFileLoader)
                    .visit(
                            Advice.to(GLInterceptor.class).on(nameStartsWith("gl")))
                    .make()
                    .load(
                            classLoader,
                            strategy);
        }
    }
    private static class GLInterceptor {

        @Advice.OnMethodExit
        public static void intercept() {
            final var builder = new StringBuilder();

            var code = 0;
            while (( code = GL11C.glGetError() ) != GL11C.GL_NO_ERROR) {
                builder.append("  - ");
                builder.append(switch (code) {
                    case GL11C.GL_INVALID_ENUM -> "Invalid Enum";
                    case GL11C.GL_INVALID_OPERATION -> "Invalid Operation";
                    case GL11C.GL_INVALID_VALUE -> "Invalid Value";
                    case GL11C.GL_STACK_OVERFLOW -> "Stack Overflow";
                    case GL11C.GL_STACK_UNDERFLOW -> "Stack Underflow";
                    case GL11C.GL_OUT_OF_MEMORY -> "Out Of Memory";
                    case GL30C.GL_INVALID_FRAMEBUFFER_OPERATION -> "Invalid Framebuffer Operation";
                    default -> "???";
                });

                builder.append("\n");
            }

            if (builder.length() > 0)
                throw new RuntimeException("OpenGL ERRORS:\n\n%s".formatted(builder.toString()));
        }
    }
}
