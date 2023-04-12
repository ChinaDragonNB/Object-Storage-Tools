package com.ak47007.minio.autoconfig;

import com.ak47007.core.EnableObjectStorage;
import com.ak47007.core.ObjectStorage;
import com.ak47007.core.dao.ObjectStorageDAO;
import com.ak47007.core.exception.ObjectStorageException;
import com.ak47007.minio.support.MinioObjectStorage;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * @Author AK47007
 * @Date 2023/4/12 22:22
 * @Description: Minio自动装配
 */
@Configuration
@EnableConfigurationProperties(ObjectStorageMinioProperties.class)
@ConditionalOnProperty(prefix = "object-storage", name = "type", havingValue = "minio")
@ConditionalOnBean(annotation = EnableObjectStorage.class)
@AllArgsConstructor
public class ObjectStorageMinioAutoConfiguration {

    private final ObjectStorageDAO objectStorageDAO;

    private final ObjectStorageMinioProperties objectStorageMinioProperties;

    @Bean
    @ConditionalOnMissingBean //  @ConditionalOnMissingBean 避免重复注册该Bean
    public ObjectStorage objectStorage() throws KeyManagementException {
        objectStorageMinioProperties.checkConfig();
        return new MinioObjectStorage(objectStorageDAO, objectStorageMinioProperties, minioClient());
    }

    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient() throws KeyManagementException {
        objectStorageMinioProperties.checkConfig();
        // Create a minioClient with the MinIO server playground, its access key and secret key.
        MinioClient client = MinioClient.builder()
                .endpoint(objectStorageMinioProperties.getEndpoint())
                .httpClient(Objects.requireNonNull(getUnsafeOkHttpsClient()))
                .credentials(objectStorageMinioProperties.getAccessKey(), objectStorageMinioProperties.getSecretKey())
                .build();
        try {
            // Make bucket if not exist.
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(objectStorageMinioProperties.getBucket()).build());
            // Bucket不存在自动创建
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(objectStorageMinioProperties.getBucket()).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ObjectStorageException("Bucket check error");
        }
        return client;
    }

    public static OkHttpClient getUnsafeOkHttpsClient() throws KeyManagementException {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };


            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            X509TrustManager trustManager = Platform.get().platformTrustManager();
            builder.sslSocketFactory(sslSocketFactory, trustManager);


            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            return builder.build();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}
