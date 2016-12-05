%takes a vector and plots an image
function [] = plotImage(v)
expectedImageSize = 28;

if(numel(v) ~= expectedImageSize*expectedImageSize)
    error(['Argument must be a vector of length ' num2str(expectedImageSize^2) ' (for a ' num2str(expectedImageSize) 'x' num2str(expectedImageSize) ' image']);
else
    v = reshape(v,expectedImageSize,expectedImageSize);
    %figure;
    imagesc(v)
    colormap gray
end