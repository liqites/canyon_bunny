attribute vec4 a_position;
// in vec4 a_position;

attribute vec4 a_color;
// in vec4 a_color;

attribute vec2 a_textCoord0;
// in vec2 a_textCoord0;

varying vec4 v_color;
varying vec2 v_textCoords;
uniform mat4 u_projTrans;

void main() {
   v_color = a_color;
   v_textCoords = a_textCoord0;
   gl_Position = u_projTrans * a_position;
}