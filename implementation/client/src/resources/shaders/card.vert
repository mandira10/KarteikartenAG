layout (location = 0) in vec3 vPositions;
layout (location = 1) in vec2 vTexCoords;
layout (location = 2) in vec3 vNormals;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

out vec2 texcoord;
out float normalz;

void main() 
{
    gl_Position = vec4(vPositions, 1.0) * transformationMatrix * viewMatrix * projectionMatrix;
    normalz = vNormals.z;
    texcoord = vTexCoords;
}