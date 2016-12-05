%% Initialise Work Space

close all;
clc; clear;

%% Variables Used in Task 4

% k         K Neighbour Count
% pFlip     Probability of Flip 
% X         N * 3 Noisless Circle in Square (CIS) Dataset

    
%   Initialize Dataset Generation Variables
    pFlip = 0.1;

%   Initialize Parzen Window Parameters
    spreadValues = [.001 0.1 0.75];%[.25 0.5 0.75];
    unknown_label = strjoin({'-'});
    trainingPatternCount = [10 30 50 75 100];
    kernel_types = {'Gaussian', 'Squared Sinc'};    
    
%   Initialize Task Parameters  
    spreadValuesLength = length(spreadValues);
    kernel_types_Length = length(kernel_types);
    trainingPatternLength = length(trainingPatternCount);

%%  Start Task 4
   
    for i = 1 : kernel_types_Length
       
        for j = 1 : trainingPatternLength
           
        %   Intitialize Training Data Count
            trainingDataCount = trainingPatternCount(j);
            
        %   Generate CIS Data
            xTrain = generateCISData(trainingDataCount);
            
        %   Generate NCIS Data
            xTrain = generateNCISData(xTrain,pFlip);
                
        %   Create Test Dataset
            [x1, x2] = meshgrid(min(xTrain(:,1)):0.01:max(xTrain(:,1)),min(xTrain(:,2)):0.01:max(xTrain(:,2)));
            xTest = [x1(:) x2(:)];
            
        %   Index g is used to keep count of sub-graphs being plotted        
            g = 1;
            figure        
                
            for k = 1 : spreadValuesLength                
                                           
            %   Run Parzen Window Algorithm
                [Ypred,PCP] = pwc_classify(xTest,xTrain,kernel_types(i),spreadValues(k),unknown_label);
                          
            %   Reshape Ypred Values
                Ypred = reshape(Ypred,size(x1,1),size(x1,2));

            %   Reshape Ypred Values
                PCPClass1 = reshape(PCP(:,1),size(x1,1),size(x1,2));
            
            %% Plot Graph  
                
                subplot(2,3,g);
                
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
                    currentTrueIndex = uniqueClassValues(l)== xTrain(:,end);
                    
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
                
                xlabel('x1');
                ylabel('x2');                
                title(sprintf('%s Kernel \n  Training Patterns = %d Spread = %.2f' ,...
                        strjoin(kernel_types(i)), trainingDataCount, spreadValues(k)));
                    
            %%  Plot Posterior Class Probability of Class 1
            
                subplot(2,3,(g+3));
            
            %   Reshape Ypred Values
                PCP1 = reshape(PCP(:,1),size(x1,1),size(x1,2));
                surfc(x1, x2,PCP1);

                xlabel('x1');
                ylabel('x2');                
                title(sprintf('Class 1 PCP\n%s Kernel \n  Training Patterns = %d Spread = %.3f' ,...
                        strjoin(kernel_types(i)), trainingDataCount, spreadValues(k)));
            
            %   Increment Graph #
                g = g + 1;
                
                               
            end
        end
    end
    

    
