pipeline {
    agent any
    
    environment {
        // 服务器配置
        SERVER_HOST = '106.15.90.212'
        SERVER_USER = 'root'
        SERVER_PASSWORD = credentials('server-password')
        
        // 部署路径
        BACKEND_DEPLOY_DIR = '/opt/smart-order-hub/backend'
        FRONTEND_DEPLOY_DIR = '/var/www/admin'
        LOG_DIR = '/var/log/smart-order-hub'
        
        // 应用配置
        JAR_NAME = 'smart-order-hub-1.0.0.jar'
        JAR_PORT = '8081'
        
        // 构建配置
        MAVEN_OPTS = '-Dmaven.test.skip=true'
        NODE_ENV = 'production'
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }
    
    stages {
        stage('代码检出') {
            steps {
                script {
                    echo '========================================'
                    echo '开始检出代码'
                    echo '========================================'
                    checkout scm
                    echo '代码检出完成'
                }
            }
        }
        
        stage('环境检查') {
            steps {
                script {
                    echo '========================================'
                    echo '检查构建环境'
                    echo '========================================'
                    sh 'java -version'
                    sh 'mvn -version'
                    sh 'node -v'
                    sh 'npm -v'
                    echo '环境检查完成'
                }
            }
        }
        
        stage('后端打包') {
            steps {
                script {
                    echo '========================================'
                    echo '开始打包后端'
                    echo '========================================'
                    dir('backend') {
                        sh """
                            echo '清理旧构建...'
                            mvn clean
                            
                            echo '开始编译打包...'
                            mvn package ${MAVEN_OPTS}
                            
                            echo '后端打包完成'
                            ls -lh target/${JAR_NAME}
                        """
                    }
                }
            }
        }
        
        stage('前端打包') {
            steps {
                script {
                    echo '========================================'
                    echo '开始打包前端'
                    echo '========================================'
                    dir('pc-admin') {
                        sh """
                            echo '安装依赖...'
                            npm install --registry=https://registry.npm.taobao.org
                            
                            echo '开始构建...'
                            npm run build
                            
                            echo '前端打包完成'
                            ls -lh dist/
                        """
                    }
                }
            }
        }
        
        stage('部署后端') {
            steps {
                script {
                    echo '========================================'
                    echo '开始部署后端'
                    echo '========================================'
                    sh """
                        # 创建部署目录
                        echo '创建部署目录...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'mkdir -p ${BACKEND_DEPLOY_DIR} ${LOG_DIR}'
                        
                        # 备份旧版本
                        echo '备份旧版本...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && if [ -f ${JAR_NAME} ]; then cp ${JAR_NAME} ${JAR_NAME}.backup; fi'
                        
                        # 停止旧服务
                        echo '停止旧服务...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && ./stop.sh || true'
                        
                        # 上传jar包
                        echo '上传jar包...'
                        sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no backend/target/${JAR_NAME} ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/
                        
                        # 上传启动脚本
                        echo '上传启动脚本...'
                        sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no scripts/backend-start.sh ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/start.sh
                        sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no scripts/backend-stop.sh ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/stop.sh
                        
                        # 启动新服务
                        echo '启动新服务...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && chmod +x start.sh stop.sh && ./start.sh'
                        
                        echo '后端部署完成'
                    """
                }
            }
        }
        
        stage('部署前端') {
            steps {
                script {
                    echo '========================================'
                    echo '开始部署前端'
                    echo '========================================'
                    sh """
                        # 创建部署目录
                        echo '创建部署目录...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'mkdir -p ${FRONTEND_DEPLOY_DIR}'
                        
                        # 备份旧版本
                        echo '备份旧版本...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${FRONTEND_DEPLOY_DIR} && if [ -f index.html ]; then tar -czf backup-\$(date +%Y%m%d-%H%M%S).tar.gz *; fi'
                        
                        # 清空旧文件
                        echo '清空旧文件...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'rm -rf ${FRONTEND_DEPLOY_DIR}/*'
                        
                        # 上传前端文件
                        echo '上传前端文件...'
                        sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no -r pc-admin/dist/* ${SERVER_USER}@${SERVER_HOST}:${FRONTEND_DEPLOY_DIR}/
                        
                        # 设置权限
                        echo '设置文件权限...'
                        sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'chown -R nginx:nginx ${FRONTEND_DEPLOY_DIR}'
                        
                        echo '前端部署完成'
                    """
                }
            }
        }
        
        stage('健康检查') {
            steps {
                script {
                    echo '========================================'
                    echo '开始健康检查'
                    echo '========================================'
                    sh """
                        # 等待服务启动
                        echo '等待服务启动...'
                        sleep 30
                        
                        # 检查后端服务
                        echo '检查后端服务...'
                        for i in {1..6}; do
                            if curl -f http://${SERVER_HOST}:${JAR_PORT}/api/health; then
                                echo '后端服务正常'
                                break
                            else
                                echo "等待后端服务启动... (\$i/6)"
                                sleep 10
                            fi
                        done
                        
                        # 检查前端服务
                        echo '检查前端服务...'
                        if curl -f http://${SERVER_HOST}/admin/; then
                            echo '前端服务正常'
                        else
                            echo '前端服务异常'
                            exit 1
                        fi
                        
                        echo '健康检查通过'
                    """
                }
            }
        }
        
        stage('清理构建') {
            steps {
                script {
                    echo '========================================'
                    echo '清理构建产物'
                    echo '========================================'
                    cleanWs()
                    echo '构建产物已清理'
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo '========================================'
                echo '构建完成'
                echo '========================================'
                echo "构建状态: ${currentBuild.result}"
                echo "构建时间: ${currentBuild.durationString}"
            }
        }
        
        success {
            script {
                echo '========================================'
                echo '部署成功！'
                echo '========================================'
                emailext (
                    subject: '✅ 智能点餐系统部署成功',
                    body: """
                        <h2>部署成功</h2>
                        <table border="1" cellpadding="10" cellspacing="0">
                            <tr><td><b>项目：</b></td><td>智能点餐系统</td></tr>
                            <tr><td><b>环境：</b></td><td>生产环境</td></tr>
                            <tr><td><b>部署时间：</b></td><td>${new Date()}</td></tr>
                            <tr><td><b>构建号：</b></td><td>${env.BUILD_NUMBER}</td></tr>
                            <tr><td><b>构建耗时：</b></td><td>${currentBuild.durationString}</td></tr>
                            <tr><td><b>后端地址：</b></td><td><a href="http://${SERVER_HOST}:${JAR_PORT}">http://${SERVER_HOST}:${JAR_PORT}</a></td></tr>
                            <tr><td><b>前端地址：</b></td><td><a href="http://${SERVER_HOST}/admin/">http://${SERVER_HOST}/admin/</a></td></tr>
                        </table>
                        <p><a href="${env.BUILD_URL}">查看构建详情</a></p>
                    """,
                    to: '624140273@qq.com',
                    mimeType: 'text/html'
                )
            }
        }
        
        failure {
            script {
                echo '========================================'
                echo '部署失败！'
                echo '========================================'
                emailext (
                    subject: '❌ 智能点餐系统部署失败',
                    body: """
                        <h2>部署失败</h2>
                        <table border="1" cellpadding="10" cellspacing="0">
                            <tr><td><b>项目：</b></td><td>智能点餐系统</td></tr>
                            <tr><td><b>环境：</b></td><td>生产环境</td></tr>
                            <tr><td><b>部署时间：</b></td><td>${new Date()}</td></tr>
                            <tr><td><b>构建号：</b></td><td>${env.BUILD_NUMBER}</td></tr>
                            <tr><td><b>失败阶段：</b></td><td>${currentBuild.currentResult}</td></tr>
                        </table>
                        <p><a href="${env.BUILD_URL}console">查看构建日志</a></p>
                        <p>请检查错误信息并修复后重新部署</p>
                    """,
                    to: '624140273@qq.com',
                    mimeType: 'text/html'
                )
            }
        }
        
        unstable {
            script {
                echo '构建不稳定，请检查'
                emailext (
                    subject: '⚠️ 智能点餐系统构建不稳定',
                    body: """
                        <h2>构建不稳定</h2>
                        <p>项目：智能点餐系统</p>
                        <p>环境：生产环境</p>
                        <p>构建时间：${new Date()}</p>
                        <p><a href="${env.BUILD_URL}console">查看构建日志</a></p>
                    """,
                    to: '624140273@qq.com',
                    mimeType: 'text/html'
                )
            }
        }
    }
}
