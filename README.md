# Java-Digital-Signature
Java command line  tool for digital signature

```bash
# help
> java -jar signer.jar -h

Usage: PKCS#11 Digital Signature Tool [options] [command] [command options]
  Options:
    -h, --help
      show usage
  Commands:
    cades      CAdES sign format
      Usage: cades [options] FileToSign
        Options:
          -h, --help
            show usage
          -d, --driver
            PKCS#11 Driver
          -u, --key-usage
            show key usage
            Default: false
          -i, --info-certificates
            show certificates info
            Default: false

    pades      PAdES sign format
      Usage: pades [options] FileToSign
        Options:
          -h, --help
            show usage
          -d, --driver
            PKCS#11 Driver
          -u, --key-usage
            show key usage
            Default: false
          -i, --info-certificates
            show certificates info
            Default: false
          -v, --visible-signature
            add visible signature - only text
            Default: false
          -vi, --visible-signature-image
            add visible signature - text and image
    
```


-----
#### Dependencies: 

- Digital Signature Services (DSS): https://github.com/esig/dss
- JCommander: https://github.com/cbeust/jcommander

-----

#### NOTE:
**Tested only on Windows.**
