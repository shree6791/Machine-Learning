function [xunit,yunit] = circumference()

%   Initialize Circle Parameters
    area = 0.5;
    x_index = 0.5;
    y_index = 0.5;
    radius = (area/pi).^.5;

%   Calculate Circle Co-ordinates on It's Circumference
    radian = 0 : pi/50 : 2*pi;
    xunit = radius * cos(radian) + x_index;
    yunit = radius * sin(radian) + y_index;

end