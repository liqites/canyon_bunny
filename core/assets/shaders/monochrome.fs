// #ifdef GL_ES
// precision mediump float;
// #endif


// // out vec4 FragColor;
// // in vec4 ourColor;

// varying vec4 v_color;
// varying vec2 v_textCoords;
// uniform sampler2D u_texture;
// uniform float u_amount;


// void main() {
//    vec4 color = v_color * texture2D(u_texture, v_textCoords);
//    float grayscale = dot(color.rgb, vec3(0.222, 0.707, 0.071));
//    color.rgb = mix(color.rgb, vec3(grayscale), u_amount);
//    gl_FragColor = color;
//    // FragColor = v_color;
// }


#ifdef GL_ES
    precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
void main() {
        vec3 color = texture2D(u_texture, v_texCoords).rgb;
        float gray = (color.r + color.g + color.b) / 3.0;
        vec3 grayscale = vec3(gray);
        gl_FragColor = vec4(grayscale, 1.0);
}