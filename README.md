# Java-Digital-Signature
Java command line  tool for digital signature PKCS#11

```
# help
> java -jar signer.jar -h

Usage: PKCS#11 Digital Signature Tool [options] [command] [command options]
  Options:
    -h, --help
      show usage
    -d, --driver
      PKCS#11 Driver
    -p, --password
      Pass password in command line (USE WITH CAUTION)
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
          -o, --output-destination
            set destination FOLDER for the output file

    pades      PAdES sign format
      Usage: pades [options] FileToSign
        Options:
          -h, --help
            show usage
          -c, --choose-certificate
            choose certificate tu use
          -o, --output-destination
            set destination FOLDER for the output file
          -v, --visible-signature
            add visible signature - only text
            Default: false
          -vi, --visible-signature-image
            add visible signature - text and image
          -f, --field-to-sign
            name of the field to sign
          -pg, --page
            page of signature  [If a field is selected this option is ignored]
            Default: 1
          -pv, --vertical-signature-position
            vertical position of visible signature: T(op) - M(iddle) -
            B(ottom) [If a field is selected this option is ignored]
          -ph, --horizontal-signature-position
            horizontal position of visible signature: L(eft) - C(enter) -
            R(ight) [If a field is selected this option is ignored]

```

-----
#### Dependencies: 

- Digital Signature Services (DSS): https://github.com/esig/dss
- JCommander: https://github.com/cbeust/jcommander

-----

#### NOTE:
**Tested only on Windows.**

**Tested with JDK8 u181**
