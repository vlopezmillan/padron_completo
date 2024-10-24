# MiRed 

## How to access to the services 

There are running the following services:

-   [The SPARQL endpoint](https://mired.uspceu.es/sparql)
-   [The Web Solr interface](https://mired.uspceu.es/solr)

Besides, you can find the [Banana dashboard](https://mired.uspceu.es/solr/banana/index.html#/dashboard/file/microrrelatos_dashboard.json) and [the documentation of the ontology](https://mired.uspceu.es/microrrelatos).

Some of theses services are accessible from console. Below is shown a query on the knowledge graph:

```console
curl -F "format=text/plain" -F "query=select ?persona {?persona a <https://mired.uspceu.es/microrrelatos#Persona>}" https://mired.uspceu.es/sparql
```  

Here are shown several queries on Solr:

```console
curl "https://mired.uspceu.es/solr/microrrelatos/select?indent=on&q=*:*"
curl "https://mired.uspceu.es/solr/microrrelatos/select?indent=on&q=g\u00E9nero_gn:mujer"
curl "https://mired.uspceu.es/solr/microrrelatos/select?indent=on&q=g\u00E9nero_gn:hombre"
``` 

An example of how to insert through the SPARQL endpoint is presented below (this requests is only valid if the SPARQL user in Virtuoso has permission to update):

```console
curl -X POST -F "format=text/plain" -F "query=PREFIX mcr: <https://mired.uspceu.es/microrrelatos#> INSERT DATA { GRAPH <http://mired.uspceu.es/microrrelatos> {mcr:LaMicrobiblioteca a mcr:Blog} } " https://mired.uspceu.es/sparql
```


[Solr documentation](https://lucene.apache.org/solr/guide/8_8/uploading-data-with-index-handlers.html) explains how to update collections.

To know the mapping between the ontology and Solr schema, you can consult `./knowledge_graph/mapping.ttl` and `./knowledge-graph/listado_mariano_openrefine.csv`. The fields in Solr are those with `_gs`, `_gn`, etc.

## MiRed architecture

The following diagram shows the different components of the system:

![MiRed architecture.](images/arquitectura_mired.svg)

Apache listens on ports 80, 443 and 8890. Requests arriving through 8890 are
met by Virtuoso container. The rest of the requests are resent to port 8008,
where Nginx container is listening. This proxy resends the requests to the
SPARQL endpoint and to `solr_banana` container. The ontology documentation is
directly served by Apache. 

The external communications are encrypted according to [Let's Encrypt](https://letsencrypt.org/)
certificate.

## Computer where MiRed is running

The system is running in a virtual machine at the [USP-CEU](https://www.uspceu.com/) data center with the following features:

-   3 processors Intel(R) Xeon(R) CPU X5650 @ 2.67GHz.
-   16 GB RAM.
-   256 GB SDD HD.
-   Ubuntu 20.04 LTS. 

## Steps to install the system in your server

Along the following steps, you have to replace `yourPassword` by the password
that you will use for Virtuoso Openlink and `mired.uspceu.es` by the server in which
you will install the system.

This instructions are valid for Ubuntu 20.04 LTS.

### Install Apache

The steps to carry out are presented below:

First of all, install Apache

```console
sudo apt install apache2
```

Open 80 TCP port:

```console
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8890/tcp
sudo ufw allow 8983/tcp
```

Test the accessibility to the index Web page. You should write in your remote browser `http://mired.uspceu.es`

### Install certificate

[Let's Encrypt](https://letsencrypt.org/) is a free, automated, and open
certificate authority brought to you by the nonprofit [Internet Security
Research Group (ISRG)](https://www.abetterinternet.org/). Its certificates are
only valid for visible public IPs. The names must be registered in DNS and
point to the IP of the machine where the web project is hosted.

Certbot is a utility that allows you to create and maintain Let's Encrypt
certificates. In the process of generating of the certificate, it can modify
the Apache or Nginx configuration files for you, but you can also manage only
the generation of the certificate, and manually certificate generation, and
manually do the web server configuration. For example, to create and install in
Apache a certificate for the domain domain `gitlab.eps.ceu.es` you have to run:

```console
sudo snap install certbot --classic
sudo certbot --apache -d mired.uspceu.es 
``` 

The answer will be something like this:

```
IMPORTANT NOTES:
 - Congratulations! Your certificate and chain have been saved at:
   /etc/letsencrypt/live/mired.uspceu.es/fullchain.pem
   Your key file has been saved at:
   /etc/letsencrypt/live/mired.uspceu.es/privkey.pem
   Your certificate will expire on 2021-06-02. To obtain a new or
   tweaked version of this certificate in the future, simply run
   certbot again with the "certonly" option. To non-interactively
   renew *all* of your certificates, run "certbot renew"
 - If you like Certbot, please consider supporting our work by:

   Donating to ISRG / Let's Encrypt:   https://letsencrypt.org/donate
   Donating to EFF:                    https://eff.org/donate-le
``` 

The following files in `/etc/apache2/sites-available` have been created:

```
mired.uspceu.es.conf
mired.uspceu.es-le-ssl.conf
``` 

They redirect http requests to https and link to the certificate and keys created,
and restarts the server.

If you do not want the `.conf` files to be modified, you can use the `certonly`
option.

You can also create certificates that have more than one name associated with
them, by adding all the necessary ones separated by commas (for example, `-d
www.ceu.es,www.ceu.com,ceu.es,ceu.com`).

Once set up, you have to create a cron to renew the certificates automatically
(these certificates are valid for 3 months, and can be renewed after the second
month). This is also done with the Certbot script (changes the certificate and
restarts the server). For example, for Apache:

```console
certbot renew --apache
``` 

The answer will be something like this:

```
Saving debug log to /var/log/letsencrypt/letsencrypt.log

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
Processing /etc/letsencrypt/renewal/mired.uspceu.es.conf
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
Cert not yet due for renewal

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
The following certificates are not due for renewal yet:
  /etc/letsencrypt/live/mired.uspceu.es/fullchain.pem expires on 2021-06-02 (skipped)
No renewals were attempted.
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
```

### Install docker
 
Install the package `docker`:

```console
sudo snap install docker
```

Check the installation:

```console
docker --version
```

Assign my user to docker group:

```console
sudo usermod -a -G docker $USER
```

Now, we have to restart the machine.

### Start services

The steps to carry out are

1. Test if the name of your machine is resolved as the public or the private
   IP. If the first case happens, then you have to modify the `/etc/hosts` so
   that the private IP is interpreted as the name of the machine, that is, as
   follows:

```
10.210.228.7 mired.uspceu.es
```

The reason is because when your proxy has to translate names with ports (e.g.
`mired.uspceu.es:8890/sparql`) to names without ports (e.g.
`mired.uspceu.es/sparql`), the requests have to go outside to resolve the name
according to the rules of your institutional network. Institutions have no
problem to receive requests with URLs that include ports, but they usually
disallow output requests to ports that are not included in a very restricted
set (80, 443, etc.). 

2.  Grant permissions on `cores` folder: 

```console
chmod a+w cores
``` 

3.  Run `start.sh`:

```console
./start.sh yourPassword
``` 

As you can see in `docker-compose.yml`, the frontend container will listen on
port 8008. 

### Redirect from Apache to the Docker frontend and to the Virtuoso container

You have to add the following virtual host specification to the file
`mired.uspceu.es.conf` in `/etc/apache2/sites-available`:

```
<VirtualHost *:8890>
    ServerAdmin webmaster@localhost
    DocumentRoot /var/www/html
    ServerName mired.uspceu.es
    ServerAlias mired.uspceu.es
    ErrorLog ${APACHE_LOG_DIR}/error.log
    CustomLog ${APACHE_LOG_DIR}/access.log combined
    RewriteEngine on
    RewriteCond %{SERVER_NAME} =mired.uspceu.es
    RewriteRule ^ https://%{SERVER_NAME}%{REQUEST_URI} [END,NE,R=permanent]
</VirtualHost>
``` 

And you have to add the following lines to the specification of the virtual host listening on 443:

```
ServerName mired.uspceu.es
ServerAlias mired.uspceu.es
ProxyPreserveHost On
ProxyRequests Off

ProxyPass / http://localhost:8008/
ProxyPassReverse / http://localhost:8008/
```   

### Create the knowledge graph:

The steps are (if the knowledge graph is not still created):

1.  Charge the ontology using the Virtuoso Openlink Web interface(`mired.uspceu.es:8890`), the sequence of menu options is: 

```
    Conductor (left menu) > linked data (upper menu) > Quad store upload
```

When _conductor_ option is chosen, the system asks for a user
`dba` and password (the one established when the service has
been started with Docker, referred in the previous example as
`yourPassword`).

Once the form has been filled in, click on _accept_. The user may
have the impression of the system has done nothing. However, the
creation of the new graph can be checked following this sequence

```
    Graph > graphs tab
```

2.  Create the set of instances of the ontology:

    2.1.    Install RDFizer:

```console
python3 -m pip install rdfizer
```

    2.2.    When it is installed the first time, the following sentence has to be run:

```console
python3 -m pip install rdfizer --use-feature=2020-resolver
```

    2.3.    Go the folder when the `config.ini` file is, and run RDFizer:

```console
cd knowledge_graph
python3 -m rdfizer -c config.ini
cd -
```

3.  Insert instances in the ontology:

If you want to insert the data created about flash fiction in Spanish, you can
run the following sentence from console:

```console
cd knowledge_graph/output
cd knowledge_graph/output/
curl -X POST --digest -u dba:yourPassword -H Content-Type:text/turtle \
     -T instancias_microrrelatos.nt -G http://mired.uspceu.es:8890/sparql-graph-crud-auth \
     --data-urlencode graph=http://mired.uspceu.es/microrrelatos
cd -
``` 

4.  Set query by default through Virtuoso conductor:

```
System Admin > Parameters > SPARQL
```  

Change the `DefaultQuery`parameter value to:

```
prefix mcr:<https://mired.uspceu.es/microrrelatos#>  select ?nombre_autor ?titulo_micro ?medio_digital_de_publicacion{?autor a mcr:Persona . ?autor rdfs:label ?nombre_autor . ?micro mcr:esObraArtisticaCreadaPor ?autor . ?micro rdfs:label ?titulo_micro . ?micro mcr:tieneMedioDigitalDePublicacion ?medio . ?medio rdfs:label ?medio_digital_de_publicacion } limit 100
``` 

5.  Publish the ontology:

```console
sudo rm -rf /var/www/html/microrrelatos
sudo cp -r knowledge_graph/ontology /var/www/html/microrrelatos
``` 

### Create the collection of data in Solr:

Here, we present the creation of a core for flash fiction ("microrrelatos") if
it is not still created. In any case, the folders have to have the appropriate
owner.

1. Create a directory with the name of the core

```console
sudo mkdir cores/microrrelatos
``` 

2. Grant all privileges to this directory

```console
sudo chmod -R +777 cores/microrrelatos
```

3. Copy the 'conf' folder into that directory

```console
sudo cp -r conf cores/microrrelatos/
``` 

4. Select Core Admin/Add core in the Web  Solr admin (`https://mired.uspceu.es/solr`)
   with the following parameters
   (`https://github.com/librairy/api/tree/master/src/test/docker/repo/cores`):

```
name: the name of the core, it should be the same that the directory (e.g. microrrelatos)
instanceDir: the name of the core, it should be the same that the directory (e.g. microrrelatos)
dataDir: the name of the core, it should be the same that the directory (e.g. microrrelatos)
config: <empty>
schema: <empty>
``` 

5.  Load the data:

The following sentence can be executed from console:

```console
curl 'http://mired.uspceu.es/solr/microrrelatos/update?commit=true' --data-binary @resources/listado_mariano.csv -H 'Content-type:application/csv'
```

### Obtain the dashboard 

Banana is accessible by means of `https://mired.uspceu.es/solr/banana/index.html`

Now, you carry out the following steps:

1.  Create the dashboard.
2.  Click on the disk icon to save it as `banana/app/dashboards/microrrelatos_dashboard.json`
3.  Accept.

## How to protect the administration

### Virtuoso

Through the interface administration:

```
Conductor > System Admin > User Accounts > Users > SPARQL (edit) > Account roles << SPARQL_UPDATE
```

### Solr

See the documentation for the [Solr basic authentication plugin](https://lucene.apache.org/solr/guide/8_8/basic-authentication-plugin.html).

## Acknowledgements 

[Carlos Badenes Olmedo](https://github.com/cbadenes) has gladly answered all the questions I have asked him.
[Raúl García García](https://es.linkedin.com/in/perfilrgg) and [Teodoro Rojo Aladro](http://ww2.uspceu.es/aplpro/pg/verpg.aspx?cip=NDIxNjY4NTE=&tit=GST) have provided very valuable contributions to the deployment of the system.

However, any deficiency is just attributable to the author.
