%% Load and display CURRENT waveforms
   
close all; clear; clc; 
load('SpikeSortingData.mat');      
       
%%  MY PCA : Covariance Matrix

eigenWavesCount = 3;
[eigenValue,eigenVector,meanX] = MyPCA(waveforms,eigenWavesCount);  
       
%% Projected Waveforms

projectedWaveform = PCAProjection(waveforms,meanX,eigenVector); 

%% Reconstructed Waveforms

reconstructedWaveform = PCAReconstruction(eigenVector,meanX,projectedWaveform);   
   
%% K Means Algorithm

figure(1)
neuronCount = 3;
uniqueColor = lines(neuronCount);

class = kmeans(projectedWaveform,neuronCount);
uniqueLabels = unique(class);


for i = 1:neuronCount

    currentClassRowIndex = class == uniqueLabels(i);
    currentClassWaveform = waveforms(currentClassRowIndex);
 
    subplot(neuronCount,1,i)
    plot(currentClassWaveform','color',uniqueColor(i,:))   
    title (sprintf('Waveform Corresponding to Neuron %i ', i))
    
end

%% Plot Input and Output Graphs
figure(2)

subplot(2,2,1)  
plot(waveforms')    
title ('Input Waveform')  

subplot(2,2,2)  
plot (eigenVector);
title (sprintf('%i Eigen Waves',eigenWavesCount));

subplot(2,2,3)  
plot (projectedWaveform(:,1),projectedWaveform(:,2),'*');
title (sprintf('Projected Waveform with %i Principal Components',eigenWavesCount));

subplot(2,2,4)  
plot(reconstructedWaveform')
title ('Reconstructed Waveforms')

%% Plot MSE Error

figure(3)
stepSize = 5;
N = size(waveforms,2);
indexLength = N/stepSize;
meanSquareError  = zeros((indexLength),1);

for i = 1 : indexLength
    
    eigenWavesCount = stepSize;    
    [eigenValue,eigenVector,meanX] = MyPCA(waveforms,eigenWavesCount);
    projectedWaveform = PCAProjection(waveforms,meanX,eigenVector);
    
    meanSquareError(i) = norm (projectedWaveform-waveforms(:,1:stepSize));
    stem(stepSize,meanSquareError(i));    
    stepSize = stepSize + 5;
    hold on

end

ylabel ('Mean Square Error');
xlabel('Eigen Vectors Count');
title('MSE vs Eigen Vectors Used');
