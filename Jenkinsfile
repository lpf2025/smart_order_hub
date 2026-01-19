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
        
        // 应用配置
        JAR_NAME = 'smart-order-hub-1.0.0.jar'
        JAR_PORT = '8081'
        
        // 域名配置
        ADMIN_DOMAIN = 'smartcan.com.cn'
        MINIPROGRAM_DOMAIN = 'smartcan.store'
    }
    
    stages {
        stage('代码检出') {
            steps {
                echo '开始检出代码...'
                checkout scm
            }
        }
        
        stage('后端打包') {
            steps {
                echo '开始打包后端...'
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('前端打包') {
            steps {
                echo '开始打包前端...'
                dir('pc-admin') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }
        
        stage('部署后端') {
            steps {
                echo '开始部署后端...'
                sh """
                    # 创建部署目录
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'mkdir -p ${BACKEND_DEPLOY_DIR}'
                    
                    # 停止旧服务
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && ./stop.sh || true'
                    
                    # 上传jar包
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no backend/target/${JAR_NAME} ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/
                    
                    # 上传启动脚本
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no scripts/backend-start.sh ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/start.sh
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no scripts/backend-stop.sh ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/stop.sh
                    
                    # 启动新服务
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && chmod +x start.sh stop.sh && ./start.sh'
                    
                    echo '后端部署完成'
                """
            }
        }
        
        stage('部署前端') {
            steps {
                echo '开始部署前端...'
                sh """
                    # 创建部署目录
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'mkdir -p ${FRONTEND_DEPLOY_DIR}'
                    
                    # 清空旧文件
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'rm -rf ${FRONTEND_DEPLOY_DIR}/*'
                    
                    # 上传前端文件
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no -r pc-admin/dist/* ${SERVER_USER}@${SERVER_HOST}:${FRONTEND_DEPLOY_DIR}/
                    
                    echo '前端部署完成'
                """
            }
        }
        
        stage('健康检查') {
            steps {
                echo '开始健康检查...'
                sh """
                    # 等待服务启动
                    sleep 30
                    
                    # 检查后端服务 - 使用端口检查
                    nc -z -w5 ${SERVER_HOST} ${JAR_PORT} || exit 1
                    
                    # 检查前端服务 - 使用端口检查
                    nc -z -w5 ${SERVER_HOST} 8082 || exit 1
                    
                    echo '健康检查通过'
                """
            }
        }
    }
    
    post {
        success {
            echo '部署成功！'
            emailext (
                subject: '✅ 智能点餐系统部署成功',
                body: """
                    <h2>部署成功</h2>
                    <p>项目：智能点餐系统</p>
                    <p>环境：生产环境</p>
                    <p>部署时间：${new Date()}</p>
                    <p>后端地址：http://${SERVER_HOST}:${JAR_PORT}</p>
                    <p>前端地址：http://${SERVER_HOST}/admin/</p>
                """,
                to: 'your-email@example.com',
                mimeType: 'text/html'
            )
        }
        failure {
            echo '部署失败！'
            emailext (
                subject: '❌ 智能点餐系统部署失败',
                body: """
                    <h2>部署失败</h2>
                    <p>项目：智能点餐系统</p>
                    <p>环境：生产环境</p>
                    <p>部署时间：${new Date()}</p>
                    <p>请检查Jenkins日志获取详细信息</p>
                """,
                to: 'your-email@example.com',
                mimeType: 'text/html'
            )
        }
    }
}
