const express = require('express');
const app = express();
const port = 3000;

app.get('/', (req, res) => {
  res.send('🚀 Hello from your CI/CD app on Kubernetes!');
});

app.listen(port, () => {
  console.log(`App running at http://localhost:${port}`);
});
