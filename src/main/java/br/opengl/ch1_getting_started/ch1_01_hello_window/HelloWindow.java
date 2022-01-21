package br.opengl.ch1_getting_started.ch1_01_hello_window;

import br.opengl.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46C.*;

final class HelloWindow {

    public static void main (String[] args) {
        OpenGL.bytebudy();

        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        final var window = glfwCreateWindow(800, 600, "LearnOpenGL", GLFW_FALSE, GLFW_FALSE);
        if (window == GLFW_FALSE) {
            System.err.println("Failed to create GLFW window");
            glfwTerminate();
            System.exit(-1);
        }


        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(window,  (windowPtr, x, y) -> glViewport(0, 0, x, y));
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        while(!glfwWindowShouldClose(window)) {
            processInput(window);

            glClear(GL_COLOR_BUFFER_BIT);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwTerminate();
    }

    static void processInput(long window) {
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }
}
