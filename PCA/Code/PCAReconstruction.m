function reconstructedInput = PCAReconstruction(eigenVector,meanX,projectedInput)
%%
% Reconstructs data points given their coordinates Y in the space spanned by the M eigenvectors of P
%
% Input:
%    Y     : MxN coordinates of the N points to (re)construct 
%    meanX : mean of data points provided by MyPCA
%    P     : DxM projection matrix containing the first M eigenvectors obtained from MyPCA
%
% Output:   
%    Ztilde     : DxN matrix containing the constructed vectors 
% 

% [D,N] = size(meanX);
M = size(eigenVector,2);
reconstructedInput =  meanX +  projectedInput * eigenVector(:,1:M)' ;

