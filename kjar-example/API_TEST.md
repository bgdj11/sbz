# Social Network Backend API - Test Endpoints

## Base URL: http://localhost:8080

### 1. Test API Status
```bash
curl -X GET http://localhost:8080/
```

### 2. Test Auth Endpoint
```bash
curl -X GET http://localhost:8080/api/auth/test
```

### 3. Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Marko",
    "lastName": "Petrovic",
    "email": "marko@example.com",
    "password": "password123",
    "city": "Belgrade"
  }'
```

### 4. Login User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "marko@example.com",
    "password": "password123"
  }'
```

### 5. Get Dashboard (requires session)
```bash
curl -X GET http://localhost:8080/api/dashboard \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID"
```

### 6. Create Post (requires session)
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID" \
  -d '{
    "content": "My first post! #hello #socialnetwork"
  }'
```

### 7. Get All Places
```bash
curl -X GET http://localhost:8080/api/places \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID"
```

### 8. Search Users and Places
```bash
curl -X GET "http://localhost:8080/api/search?q=Belgrade" \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID"
```

## Database Setup Required:
1. Install PostgreSQL
2. Create database 'sbz'
3. User: postgres, Password: super
4. Tables will be created automatically by Hibernate

## Frontend CORS Configuration:
- Allowed origins: http://localhost:3000, :4200, :8081
- All HTTP methods supported
- JSON responses for all endpoints