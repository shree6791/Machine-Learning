function [newXtrain,newXtest,newLTrain,newLtest] = splitDataSet(index,Xtrain,Ltrain)

% Create Validation Dataset of Size 1
  newXtest = Xtrain(index,:);
  newLtest = Ltrain(index);

% Duplicate Xtrain, Ltrain Dataset  
  newXtrain = Xtrain;
  newLTrain = Ltrain;

% Remove index Data from newXtrain, newXtrain to form N-1 Training Dataset
  newXtrain(index,:) = [];
  newLTrain(index) = [];

end