package br.opengl.ch1_getting_started.ch1_02_hello_triangle;

import br.opengl.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46C.*;

final class HelloTriangle {

    public static void main (String[] args) {
        OpenGL.bytebudy();

        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // GLFW: window creation
        // --------------------
        final var window = glfwCreateWindow(800, 600, "LearnOpenGL", GLFW_FALSE, GLFW_FALSE);
        if (window == GLFW_FALSE) {
            System.err.println("Failed to create GLFW window");
            glfwTerminate();
            System.exit(-1);
        }

        glfwMakeContextCurrent(window);
        // LWJGL: load all OpenGL function pointers
        // ---------------------------------------
        GL.createCapabilities();
        glfwSetFramebufferSizeCallback(window,  (windowPtr, width, height) -> glViewport(GL_ZERO, GL_ZERO, width, height));

        // build and compile our shader program
        // ------------------------------------
        final var program = glCreateProgram();
        {
            final var vertex = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    
                    void main() {
                        gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
                    }
                    """);
            glCompileShader(vertex);
            if (glGetShaderi(vertex, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.printf("Error compiling VERTEX SHADER: %s%n", glGetShaderInfoLog(vertex));

                GL.setCapabilities(null);
                glfwTerminate();
                System.exit(-1);
            }
            glAttachShader(program, vertex);

            final var fragment = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragment, """
                    #version 330 core
                    
                    out vec4 FragColor;
                    
                    void main() {
                        FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                    }
                    """);
            glCompileShader(fragment);
            if (glGetShaderi(fragment, GL_COMPILE_STATUS) == GL_FALSE) {
                System.err.printf("Error compiling FRAGMENT SHADER: %s%n", glGetShaderInfoLog(fragment));

                GL.setCapabilities(null);
                glfwTerminate();
                System.exit(-1);
            }
            glAttachShader(program, fragment);

            glLinkProgram(program);
            if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
                System.err.printf("Error linking shader program: %s%n", glGetProgramInfoLog(fragment));

                GL.setCapabilities(null);
                glfwTerminate();
                System.exit(-1);
            }
        }

        // set up vertex data (and buffer(s)) and configure vertex attributes
        // ------------------------------------------------------------------
        final var vao = glGenVertexArrays();
        glBindVertexArray(vao);

        final var vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, new float[] {
                -0.5f, -0.5f, 0.0f, // left
                0.5f, -0.5f, 0.0f, // right
                0.0f,  0.5f, 0.0f  // top
        }, GL_STATIC_DRAW);

        glVertexAttribPointer(GL_ZERO, 3, GL_FLOAT, false, GL_ZERO, GL_ZERO);
        glEnableVertexAttribArray(GL_ZERO);

        // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's bound vertex buffer object so afterwards we can safely unbind
        glBindBuffer(GL_ARRAY_BUFFER, GL_ZERO);

        // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
        // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        glBindVertexArray(GL_ZERO);

        // render loop
        // -----------
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        while(!glfwWindowShouldClose(window)) {
            processInput(window);

            glClear(GL_COLOR_BUFFER_BIT);

            glUseProgram(program);
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glUseProgram(GL_ZERO);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        // optional: de-allocate all resources once they've outlived their purpose:
        // ------------------------------------------------------------------------
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);

        // lwjgl: terminate, clearing all previously allocated
        // ---------------------------------------------------
        GL.setCapabilities(null);

        // glfw: terminate, clearing all previously allocated
        // --------------------------------------------------
        glfwTerminate();
    }

    static void processInput(long window) {
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }
}
