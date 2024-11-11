package com.spring.backend.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import com.spring.backend.Exception.InvalidAWSCredentials;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Service
@Transactional(rollbackOn = Exception.class)
public class AwsS3Service {
    private final String bucketName="goodstay23";

    @Value("${aws.s3.access.key}")
    private String AwsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String AwsS3SecretKey;



    public String saveImageToS3(MultipartFile photo){
        String s3LocationImage=null;
        try {

            if(AwsS3AccessKey == null || AwsS3SecretKey ==null || AwsS3SecretKey.isEmpty() || AwsS3AccessKey.isEmpty()){
                throw new InvalidAWSCredentials("AWS S3 access key and secret key are not set");
            }

            String s3Filename= photo.getOriginalFilename();
            BasicAWSCredentials awsCredentials=new BasicAWSCredentials(AwsS3AccessKey,AwsS3SecretKey);
            AmazonS3 amazonS3= AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(String.valueOf(Region.AP_Mumbai))
                    .build();
            InputStream inputStream=photo.getInputStream();
            ObjectMetadata metadata=new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            PutObjectRequest putObjectRequest=new PutObjectRequest(bucketName,s3Filename,inputStream,metadata);
            amazonS3.putObject(putObjectRequest);

            s3LocationImage="https://" + bucketName + ".s3.amazonaws.com/" + s3Filename;
            return s3LocationImage;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(
                    "Unable to upload image"+e.getMessage()
            );
        }
    }


}
