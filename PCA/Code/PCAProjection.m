function projectedInput = PCAProjection(x,meanX,eigenVector)
%%
% Projects a matrix of data points Z on the first M eigenvectors
%
% Input:
%    Z     : DxN data matrix (N columns of data zi of dimension D)
%    meanX : mean of data points provided by MyPCA
%    P     : DxM projection matrix containing the first M eigenvectors obtained from MyPCA
%
% Output:   
%    Y     : MxN matrix containing the components in PCA subspace for all data points of Z
% 

M = size (eigenVector,2);

projectedInput = x * eigenVector(:,1:M);

% for i = 1 : N
%     x = X (i,:);
%     projectedInput(i,:) = x' * eigenVector(:,1:M);
% end

end
    