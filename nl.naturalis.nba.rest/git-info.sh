mkdir -p ${1}/WebContent/WEB-INF/classes
echo "git.commit.id=$(git rev-parse --verify HEAD)" > ${1}/WebContent/WEB-INF/classes/git.properties
echo "git.branch=$(git rev-parse --abbrev-ref HEAD)" >> ${1}/WebContent/WEB-INF/classes/git.properties
echo "git.closest.tag.name=$(git describe --tags --abbrev=0)" >> ${1}/WebContent/WEB-INF/classes/git.properties
echo "git.build.time=$(date)" >> ${1}/WebContent/WEB-INF/classes/git.properties