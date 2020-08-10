A job recommendation website provides a platform for users to search and apply for positions. It also provides personalized position recommendations according to users' favor.
Position data is fetched from Github jobs API.<br/>
Deploy on [AWS EC2](http://18.188.168.13/job/).

## Features 
- Search jobs based on geo location
- Save interested jobs 
- Recommend similar jobs based on the saved content
- User register / login / logout

## Build With
- [Github jobs API](https://jobs.github.com/api)
- [MonkeyLearn API](https://monkeylearn.com/) - to extract keywords from job description
- [Apache Tomcat](http://tomcat.apache.org/) - Java webapp framework
- Bootstrap - CSS style
- MySQL
- [Project Lombok](https://projectlombok.org/) - Java library

## Servelts
- /search
  - GET -> load nearby jobs
- /history
  - GET -> load saved positions
  - POST -> save interested positions
  - DELETE -> delete interested positions
- /recommendation
  - GET -> search and load jobs based on saved content
- /register
  - POST -> user register
- /login
  - GET -> check user login status
  - POST -> login a user
- /logout
  - GET -> logout a user
  
## DB Tables
#### history
Field  | Key
------------- | -------------
user_id  | Primary, Foreign
item_id  | Primary, Foreign

#### items
Field  | Key
------------- | -------------
item_id  | Primary
name | 
address | 
image_url | 
url | 

#### keywords
Field  | Key
------------- | -------------
keyword  | Primary
item_id  | Primary, Foreign

#### users
Field  | Key
------------- | -------------
user_id  | Primary
password | 
first_name | 
last_name | 

## Screenshots
![job_recom_register](https://user-images.githubusercontent.com/65449903/89809746-94f21500-db6e-11ea-9cd0-ecf0386550be.png)
![job_recom_search_nearby](https://user-images.githubusercontent.com/65449903/89809750-96234200-db6e-11ea-97c7-aa60443e65c1.png)
![job_recom_fav_jobs](https://user-images.githubusercontent.com/65449903/89809733-91f72480-db6e-11ea-8bc6-accf611681d6.png)
![job_recom_recommendation](https://user-images.githubusercontent.com/65449903/89809740-93c0e800-db6e-11ea-8d04-ddb5f6c06a8e.png)




