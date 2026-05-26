#version 130

uniform vec3 camPos;
uniform vec3 center;
uniform float radius;
uniform float zNear;
uniform float zFar;
uniform float aspect;
uniform vec4 scanColor;
uniform sampler2D depthTex;

in vec3 ray;
in vec2 texCoord;

const float width = 10.0;
const float sharpness = 10.0;

float scanlines(vec2 uv) {
  return 1.0 - clamp(round(abs(fract(uv.t * 100.0) * 2.0)), 0.0, 1.0);
}

void main() {
  vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

  float depth = texture2D(depthTex, texCoord).r;
  float linearDepth = (2.0 + aspect) * zNear * zFar / (zFar + zNear - (2.0 * depth - 1.0) * (zFar - zNear));

  vec3 worldPos = camPos + linearDepth * ray;
  float dist = distance(worldPos, center);

  if (dist < radius && dist > radius - width && depth < 1.0) {
    float diff = 1.0 - (radius - dist) / width;
    vec4 outerColor = vec4(min(scanColor.rgb * 1.45, vec3(1.0)), scanColor.a);
    vec4 midColor = vec4(scanColor.rgb * 0.8, scanColor.a);
    vec4 innerColor = vec4(scanColor.rgb * 0.35, scanColor.a);
    vec4 scanlineColor = vec4(min(scanColor.gbr * 1.2, vec3(1.0)), scanColor.a);
    vec4 edge = mix(midColor, outerColor, pow(diff, sharpness));
    color = mix(innerColor, edge, diff) + scanlines(texCoord) * scanlineColor;
    color *= diff;
  }

  gl_FragColor = color;
}
