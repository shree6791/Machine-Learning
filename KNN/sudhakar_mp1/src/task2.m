%% Initialise Work Space

close all;
clc; clear;

%% Variables Used in Task 2

% k         K Neighbour Count
% pFlip     Probability of Flip 
% N         # of Data Samples in Dataset 
% xTrain    N * 3 Noisless Circle in Square (CIS) Dataset

    
%   Initialize Dataset Generation Variables
    pFlip = 0.1;
    
%   Initialize KNN Parameters
    norm_types = [2, inf];    
    kNeighbourValue = [1 5] ;
    unknown_label = strjoin({'-'});
    trainingPatternCount = [10 30 50 75 100];
        
%   Initialize Task Parameters  
    norm_types_Length = length(norm_types);
    kNeighbourLength = length(kNeighbourValue);
    trainingPatternLength = length(trainingPatternCount);
    
%%  Start Task 2

for i = 1 : norm_types_Length
    
    for j = 1 : kNeighbourLength
        
    %   Index g is used to keep count of sub-graphs being plotted
        g = 1;
        figure
        
        for k = 1 : trainingPatternLength
            
        %   Intitialize Training Data Count
            trainingDataCount = trainingPatternCount(k);
            
        %   Generate CIS Data
            xTrain = generateCISData(trainingDataCount);
            
        %   Generate NCIS Data
            xTrain = generateNCISData(xTrain,pFlip);
            
        %   Create Test Dataset
            [x1, x2] = meshgrid(min(xTrain(:,1)):0.01:max(xTrain(:,1)),min(xTrain(:,2)):0.01:max(xTrain(:,2)));
            xTest = [x1(:) x2(:)];
            
        %   Run KNN Algorithm to Classify Data
            [Ypred, PCP] = knn_classify(xTest,xTrain,kNeighbourValue(j),norm_types(i),unknown_label);
            
        %   Reshape Ypred Values
            Ypred = reshape(Ypred,size(x1,1),size(x1,2));
            
        %% Plot Graph
            
            subplot(3,2,g);
            
        %   Find Different Classes Present in Dataset : X
            uniqueClassValues = unique(xTrain(:,end));
            
        %   Find # of Different Classes Present in Dataset X
            distinctClassCount = numel(uniqueClassValues);
            
        %   Assign Color of Blue and Red to Plot Labels
            uniqueColor = [0 0 1; 1 0 0];
           
        %%   Plot Decision Regions Using Contour
            
        %   Assign Color Map of Light Blue (Class 1) and Red (Class 2) to Decision Region 
            contourf(x1,x2,Ypred);            
            colormap([0.8 0.8 1;1 0.8 0.8]);
            hold on
            
        %%  Plot Training Data
        
            for l = 1 : distinctClassCount
                
            %   Find Logical Indices of Data Samples whose Class Label = lth Class Value
                currentTrueIndex = uniqueClassValues(l)==xTrain(:,end);
                
            %   Store Data Samples whose Class Label = lth Class Value in currentClass
                currentClass = xTrain(currentTrueIndex,1:2);
                
            %   Plot Graph of currentClass
                plot(currentClass(:,1),currentClass(:,2),'*','color',uniqueColor(l,:))
                
               hold on
                                
            end
            
        %%  Plot Decision Boundary 
                           
        %   Calculate Circumference Co-ordinates
            [xunit,yunit] = circumference();
            plot(xunit,yunit,'b');

        %   Label the Graph
           g = g + 1;
           xlabel('x1');
           ylabel('x2');
           title(sprintf(' L%d Norm \n Training Patterns = %d K-Neighbours = %d ',...
                    norm_types(i), trainingDataCount, kNeighbourValue(j)));       

        end
    end
end
    