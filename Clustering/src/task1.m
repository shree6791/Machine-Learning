%% Initialise Work Space

close all;
clc; clear;

%% Variables Used in Task 1

% k         K Neighbour Count
% pFlip     Probability of Flip 
% N         # of Data Samples in Dataset 
% X         N * 3 Noisless Circle in Square (CIS) Dataset

%%   Initialize Variables for Task 1

    k = 1;
    N = 100;
    pFlip = 0.0;
    numberOfGraphs = 6;

%   Find Probabilities as Mentioned in Task 1    
    fprintf('Find Probabilities \n\twhere\n\t x = Feature Space\n\t c1 = Class1, c2 = Class 2 \n\t R1 = Region 1, R2 = Region2\n\n');
    
%   Open Window to Plot Graphs
    figure

%% Start Task 1

for j = 1 : numberOfGraphs
    
%   Generate CIS Data
    X = generateCISData(N);
    
%   Generate Noisy Data
    X = generateNCISData(X,pFlip);

%   Find Different Classes Present in Dataset : X
    uniqueClassValues = unique(X(:,end));
    
%   Find # of Different Classes Present in Dataset X
    distinctClassCount = numel(uniqueClassValues);  
    
%   Generate distinctClassCount # Random Colors    
    uniqueColor = lines(distinctClassCount);

%   Calculate Probabilities
    fprintf('When pFlip "%0.2f" \n\n', pFlip);
    probability(X);
    
%%  Plot Data

    subplot(2,3,k)
      
    for i = 1 : distinctClassCount
    
    %   Find Logical Indices of Data Samples whose Class Label = ith Class Value        
        currentTrueIndex = uniqueClassValues(i)==X(:,end);
        
    %   Store Data Samples whose Class Label = ith Class Value in currentClass
        currentClass = X(currentTrueIndex,1:2);
        
    %   Plot Graph of currentClass 
        plot(currentClass(:,1),currentClass(:,2),'*','color',uniqueColor(i,:))   
        
        hold on
           
    end
    
%%  Plot Decision Boundary 
    
    % Calculate Circumference Co-ordinates
    [xunit,yunit] = circumference();
    plot(xunit,yunit,'b');

    %Label Graph
    xlabel('x1');
    ylabel('x2');
    title(sprintf('NCIS Classification pFlip = %f',pFlip))   
    
    hold on

%%  Initialize Parameters for Next Iteration
    
%   Increment pFlip value by 0.1
    pFlip = pFlip + 0.1;

%   Increment k by 1
    k = k+1;
    
end


