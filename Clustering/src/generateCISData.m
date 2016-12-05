
function [X] = generateCISData(N)

% N = # of Data Samples in Data Set X

% Generate N*2 Matrix consisting of Random # in Range 0 - 1
X = rand(N,2);

% Add Class Value Column to N*2 Matrix
X = [X zeros(N,1)];

% Calculate Radius of Circle given it's Area
cirlceArea = 0.5;
circleRadius = (cirlceArea/pi).^.5;

% Generate N*2 Matrix consisting of Circle Centre Values
circleCenter = repmat([0.5 0.5],N,1);

% Find Distance of Each Data Sample from Center of Circle
distance = (sum((X(:,1:2) - circleCenter).^2,2)).^.5;

% When distance <= circleRadius, assign Class Value = 1
X(distance <= circleRadius,end) = 1;

% When distance > circleRadius, assign Class Value = 2
X(distance > circleRadius,end) = 2;


end