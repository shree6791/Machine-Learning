function [Lpred, Scores] = mnr_classify(Xtest, Weights)
   
%   Find # of Test Data Samples   
    N = size(Xtest,1); 

%   Add 1s Column to Test Data to Support Bias Element   
    Xtest = [ones(N,1) Xtest];

%   Find Posterior Class Probability    
    Scores = Xtest * Weights;
    
%   Find Predicted Output     
    [~,Lpred] = max(Scores,[],2);    

end