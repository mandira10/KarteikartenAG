in vec2 texcoord;
in float normalz;

out vec4 fragColor;
uniform sampler2D frontTexture;
uniform sampler2D backTexture;
uniform bool hasTexture;
uniform vec4 color;

void main() 
{
    if(normalz > 0)
        fragColor = texture(frontTexture, texcoord) * color;
    else
        fragColor = texture(backTexture, texcoord) * color;
}