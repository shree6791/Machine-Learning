close all; clear; clc; 

DataOption=2;

if DataOption==1
  usps=load('usps.mat'); 
  AllImages=usps.x; % each row is an 16 by 16 image stacked in a 1x256 line vector
  labels=usps.y;
  DimD=16;
  SelectedDigit=3;

  %% Selecting only a subset of the images
  imagesIndex=find(labels==SelectedDigit); 
  images=AllImages(imagesIndex,:);  

  % displaying some images 
  for i=1:5
    image=reshape(AllImages(i,:),DimD,DimD)'; %reshape the image to display it
    figure;
    imshow(image)
    title(strcat('image label :',num2str(labels(i))));
  end
end

if DataOption==2

  %% Loading and displaying a face
   yalefaces=load('Subset1YaleFaces.mat');
   images=yalefaces.X; %each row is a 50x50 image
   labels=yalefaces.Y;
   DimD=50;
   image=reshape(images(1,:),DimD,DimD);
   figure;
   imshow(image/255) % displaying and normalizing because imshow assumes values are between 0 and 1
   title('example of a face');

  %% Load the same faces
  data= load('SameFace.mat');
  SameFaceImages=data.images;

end

%Notice : the images are stacked in row in the above datasets
