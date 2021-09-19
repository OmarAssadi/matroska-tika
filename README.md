# Matroska Tika

This project adds a Tika detector for MKV and WebM files, allowing you to determine the correct MIME type based on the
file contents.

## Usage

1. Add Apache Tika as a dependency:

```groovy
implementation 'org.apache.tika:tika-core'
```

2. Add the Matroska Tika as a dependency. I intend on publishing this to the central repository soon.

3. Either use the Matroska detector directly:

```java
final MatroskaDetector detector=new MatroskaDetector();
final InputStream mkv=new BufferedInputStream(new FileInputStream("test.mkv"));
final MediaType type=detector.detect(mkv,new Metadata());
```

or use Tika's composite detector:

```java
final TikaConfig tika=new TikaConfig();
final Detector detector=tika.getDetector();
final InputStream mkv=new BufferedInputStream(new FileInputStream("test.mkv"));
final MediaType type=detector.detect(mkv,new Metadata());
```
