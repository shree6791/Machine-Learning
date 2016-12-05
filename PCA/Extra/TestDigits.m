 %% Load and display Test Digit
 
close all; clear; clc;
load('usps.mat');  
image1 = x(1,:); 

v = reshape(image1,16,16);
subplot(1,3,1)
imshow(v,[])
colormap gray
title('Input Digit')

%% MY PCA : Covariance Matrix

eigenFacesCount = 70;
[eigenValue,eigenVector,meanX] = MyPCA(x,eigenFacesCount);

%% Projection

projectedDigit = PCAProjection(x,meanX,eigenVector);

%% Eigen Digit

edigit1 = eigenVector(:,1);
v = reshape(edigit1,16,16);
subplot(1,3,2)
imshow(v,[])
title('Eigen Digit 1')

%% Reconstructed Digit

reconstructedDigit = PCAReconstruction(eigenVector,meanX,projectedDigit);
v = reshape(reconstructedDigit(1,:),16,16);

subplot(1,3,3)
imshow(v,[])
colormap gray
title('Reconstructed')