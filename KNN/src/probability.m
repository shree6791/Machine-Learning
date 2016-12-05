
function [] = probability(X)

% Initialize Dataset
    xLabel = X(:,end);
    xData = X(:,1:end-1);
    classValues = unique(xLabel);

% Initialize Task Variables
    area = 0.5;
    center = [0.5 0.5];
    dataCount = size(X,1);
    radius = (area/pi).^.5;
    distance = zeros(dataCount,1);

% Find Distance of Each Point from Center of Circle 
    for i = 1 : dataCount    
       distance(i) = norm((xData(i,:) - center));     
    end

%   Find # of Data Points in Region A
    regionA = distance <= radius;
    dataPointsInRegionA = sum(regionA);

%   Find # of Data Points in Region B
    regionB = distance > radius;
    dataPointsInRegionB = sum(regionB);

%   Calculate Prior Probabilities of Region A and B
    priorA = dataPointsInRegionA / dataCount;
    priorB = dataPointsInRegionB / dataCount;

    fprintf('\tp(c1)=%.2f\n ', priorA);
    fprintf('\tp(c2)=%.2f\n', priorB);

%   Find # of Class 1 and 2 Values in Region A
    regionAClassIndex = find(distance <= radius);
    class1PointsInRegionA = sum(xLabel(regionAClassIndex) == classValues(1));
    class2PointsInRegionA = sum(xLabel(regionAClassIndex) == classValues(2));

%   Find # of Class 1 and 2 Values in Region B
    regionBClassIndex = find(distance > radius);
    class1PointsInRegionB = sum(xLabel(regionBClassIndex) == classValues(1));
    class2PointsInRegionB = sum(xLabel(regionBClassIndex) == classValues(2));

%   Find Posterior Probabilities wrt Region A
    probClass1InRegionA = class1PointsInRegionA/dataPointsInRegionA;
    probClass2InRegionA = class2PointsInRegionA/dataPointsInRegionA;

    fprintf('\tp(c1|x,R1)=%.2f\n ', probClass1InRegionA);
    fprintf('\tp(c2|x,R1)=%.2f\n', probClass2InRegionA);

%   Find Posterior Probabilities wrt Region B
    probClass1InRegionB = class1PointsInRegionB/dataPointsInRegionB;
    probClass2InRegionB = class2PointsInRegionB/dataPointsInRegionB;

    fprintf('\tp(c1|x,R2)=%.2f\n ', probClass1InRegionB);
    fprintf('\tp(c2|x,R2)=%.2f\n\n', probClass2InRegionB);
    
%   Bayes Error
    bayesError = 1 - max(probClass1InRegionA,probClass2InRegionB);
    fprintf('\tBayes Error=%.2f\n\n', bayesError);
    