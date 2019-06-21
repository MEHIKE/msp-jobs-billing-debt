rm startRCA
wget http://localhost:{{server_port}}/startRCA
tail -f /opt/app/webapp/log/msp-jobs-billing-debt.out
