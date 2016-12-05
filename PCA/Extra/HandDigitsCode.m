%% Load and display Hand Written Digit

close all; clear; clc;  
digits=load('HandwrittenDigits.mat');
imageSet=digits.images;

subplot(1,3,1)
image1 = imageSet(1,:);
v = reshape(image1,28,28);
imshow(v,[])
title('Input Digit');

%% MY PCA : Covariance Matrix

eigenFacesCount = 70;
[eigenValue,eigenVector,meanX] = MyPCA(imageSet,eigenFacesCount);

%% Projection

projectedImage = PCAProjection(imageSet, meanX, eigenVector);

%% Eigen Digit

eface1 = eigenVector(:,1);
v = reshape(eface1,28,28);
subplot(1,3,2)
imshow(v,[])
title('Eigen Digit 1')

%% Reconstructed Image
reconstructedWaveform = PCAReconstruction(eigenVector,meanX,projectedImage);
r = reconstructedWaveform;
r = r(1,:);  

v = reshape(r,28,28);
subplot(1,3,3)
imshow(v,[])
colormap gray
title ('Reconstructed Digit')