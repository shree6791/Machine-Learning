function [Lpred, Scores] = da_classify(Xtest, Means,Covariances, Priors)

N = size(Xtest,1);
classCount = size(Priors,1);
Scores = zeros(N,classCount);

for i = 1 : classCount
   
    A = log(Priors(i));    
    B = log(det(Covariances{i,1}));    
    %mean = repmat(Means{i,1},1,N)';
    
    C = zeros(N,1);
    
    for j = 1 : N    
        C(j) = (Xtest(j,:)-Means{i,1}')*pinv(Covariances{i,1})*(Xtest(j,:)-Means{i,1}')';    
    end
    
    Scores(:,i) = A - 0.5*(B+C);
    
    
end

[~,Lpred] = max(Scores,[],2);

end