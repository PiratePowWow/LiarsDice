echo '***************************************************'
echo '**** starting Liars-Dice deploy... ****'
echo '***************************************************'

echo '**** stopping running service ****'

sudo initctl stop musc-smoking

echo '**** pulling changes ****'

cd ~/build/musc-smoking-cessation-api
git pull origin master

echo '**** echo creating zip archive ****'

./activator -mem 512 dist

echo '**** unzipping archive... ****'

cd ~/dist
sudo rm -rf musc-smoking-cessation-api-1.0-SNAPSHOT/
sudo unzip ~/build/musc-smoking-cessation-api/target/universal/musc-smoking-cessation-api-1.0-SNAPSHOT.zip

echo '**** starting service ****'

sudo initctl start musc-smoking

echo '****************************************************'
echo '**** riskband musc-smoking-cessation SUCCESS... ****'
echo '****************************************************'
pull changes from git
build war
run war
then you can just run something like
sudo ssh -i ~/.ssh/musc-smoking-cessation-dev.pem ec2-user@ec2-52-73-20-23.compute-1.amazonaws.com '~/deploy.sh'