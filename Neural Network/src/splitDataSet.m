function [newXtrain,newXtest,newLTrain,newLtest] = splitDataSet(first,last,Xtrain,Ltrain)

% Create Validation Dataset of Size 1
  newXtest = Xtrain(first:last,:);
  newLtest = Ltrain(first:last,:);

% Duplicate Xtrain, Ltrain Dataset  
  newXtrain = Xtrain;
  newLTrain = Ltrain;

% Remove index Data from newXtrain, newXtrain to form N-1 Training Dataset
  newXtrain(first:last,:) = [];
  newLTrain(first:last,:) = [];

end