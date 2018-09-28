# Java-Digital-Signature
Java command line  tool for digital signature PKCS#11

```bash
# help
> java -jar signer.jar -h

Usage: PKCS#11 Digital Signature Tool [options] [command] [command options]
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
  Commands:
    cades      CAdES sign format
      Usage: cades [options] FileToSign
        Options:
          -h, --help
            show usage
          -c, --choose-certificate
            choose certificate tu use

    pades      PAdES sign format
      Usage: pades [options] FileToSign
        Options:
          -h, --help
            show usage
          -c, --choose-certificate
            choose certificate tu use
          -v, --visible-signature
            add visible signature - only text
            Default: false
          -vi, --visible-signature-image
            add visible signature - text and image
          -pv, --vertical-signature-position
            vertical position of visible signature: T(op) - M(iddle) -
            B(ottom) [If a field is selected this option is ignored]
          -ph, --horizontal-signature-position
            horizontal position of visible signature: L(eft) - C(enter) -
            R(ight) [If a field is selected this option is ignored]
          -p, --page
            page of signature
            Default: 1

```

-----
#### Dependencies: 

- Digital Signature Services (DSS): https://github.com/esig/dss
- JCommander: https://github.com/cbeust/jcommander

-----

#### NOTE:
**Tested only on Windows.**

**Tested with JDK8 u181**
