# Java-Digital-Signature
Java command line tool for digital signature with PKCS#11 token.

To use the tool simply run `signer.jar`.

```
# help
> java -jar signer.jar -h

Usage: PKCS#11 Digital Signature Tool [options] [command] [command options]
  Options:
    -h, --help
      display help
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
    cades      CAdES signature format
      Usage: cades [options] FileToSign
        Options:
          -h, --help
            display help
          -c, --choose-certificate
            choose certificate to use
          -o, --output-folder
            set destination FOLDER for the output file
          -n, --newfile-name
            set name of the new file

    pades      PAdES signature format
      Usage: pades [options] FileToSign
        Options:
          -h, --help
            display help
          -c, --choose-certificate
            choose certificate to use
          -o, --output-folder
            set destination FOLDER for the output file
          -n, --newfile-name
            set name of the new file
          -v, --visible-signature
            add visible signature - only text
            Default: false
          -vi, --visible-signature-image
            add visible signature - text and image
          -s, --skip-field-selection
            skip the choice of the field to use
            Default: false
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
### Usage example

#### Token Info:

List all certificates and show purpose of the public key contained in each certificate (key usage).

```
>java -jar signer.jar -u

Password:
<certificate name>
Certificate: <i>
Key Usage:
 digitalSignature => false
 nonRepudiation => true
 keyEncipherment => false
 dataEncipherment => false
 keyAgreement => false
 keyCertSign => false
 cRLSign => false
 encipherOnly => false
 decipherOnly => false
```

For more detailed info use `-i` option.

The *jar* contain the driver used for the token in use (see **NOTE** section at the end of the Readme) and auto extract it, in the same folder of the jar, at the first run. 

It is possible to **use a specific driver** with the `-d` option.

#### CAdES (CMS Advanced Electronic Signatures):

```
# Basic usage:
>java -jar signer.jar cades test.pdf
Selected Signature Format: CADES
Start Signature Procedure
Password:

Certificate to use:  <certificate name>
Start of signing process...
Create signed file: test.pdf.p7m
End of signing process.

```

It is possible to specify folder and name of the output file with `-o` and `-n` options:

```
>java -jar signer.jar cades test.pdf -o Test -n newfile.pdf
```

#### PAdES (PDF Advanced Electronic Signatures):

- **No visible signature**

```
>java -jar signer.jar pades test.pdf
Selected Signature Format: PADES
Start Signature Procedure
Password:

Certificate to use:  <certificate name>
No available field in the pdf.
Start of signing process...
Create signed file: test-signed.pdf
End of signing process.
```

- **Visible signature** (`-v` option or `-vi` for use also an image).
If the *pdf* contains some signable fields, the tool asks to user if he wants to use one of them, else the signature is placed in the lower left corner of the first page. The position of the signature can be customized using the options `-pg`, `-pv`, `-ph` (*page, vertical postion, horizontal position*)

```
>java -jar signer.jar pades -v test.pdf
[...]

Certificate to use: <certificate name>
No available field in the pdf.
Start of signing process...
Create signed file: test-signed(1).pdf
End of signing process.

>java -jar signer.jar pades -v test-form.pdf -pg 2 -pv T -ph R
[....]

# Put Signature in the top right corner of the second page of the pdf

```

- Try to sign a document that contains signable **fields**:

```
>java -jar signer.jar pades -v test-form.pdf                                                      
[...]
Certificate to use: <certificate name> 
[0] - page:1 - Signature
[1] - page:1 - Signature_1
[2] - page:2 - Signature_2
Select Field to use (-1 or Enter for skip):0
[....]   
```
In this way the field named 'Signature' was used for contain the signature.
If the field name is already known, it can be provided with  `-f` option.
For skip this step use `-s` option.

```
>java -jar signer.jar pades -v test-form.pdf -f Signature
```

**NB:** if a field is selected, all placement options (`-pg`, `-pv`, `-ph`) are ignored. 

-----
#### Dependencies: 

- Digital Signature Services (DSS): https://github.com/esig/dss
- JCommander: https://github.com/cbeust/jcommander

-----

#### NOTE:
- Tested only on **Windows 10, 8.1 (Oracle JDK8).**

- **Tested with Bit4id smart card reader and an Italian CNS** (all provided by Aruba): 

    [Link to Aruba page](https://www.pec.it/cns-token.aspx) 
    
    [Link to Bit4id usb token](https://www.bit4id.com/en/lettore-di-smart-card-minilector-s-evo/)

![Token Image](https://www.pec.it/getattachment/20362be8-daa3-44a6-9a91-4d801245baa7/Token)
