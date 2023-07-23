import subprocess
import os


def run_jar(jar):
    print("Run jar {}".format(jar))
    subprocess.call(['java', '-jar', '-Dspring.profiles.active=local', jar])


def build_and_start_java_app(path):
    os.chdir(path)
    command = ".\gradlew.bat clean build -x test"
    subprocess.check_output(command, shell=False)
    os.chdir("..")
    libpath = r'{}\build\libs'.format(path)
    files = []

    for p in os.listdir(libpath):
        if os.path.isfile(os.path.join(libpath, p)):
            if "plain" not in p:
                files.append(p)
                print(p)
    jar = "{}\{}".format(libpath, files[0])
    print("Run jar {}".format(jar))
    t = subprocess.Popen(['java', '-jar', '-Dspring.profiles.active=local', jar])

    # t = threading.Thread(target=run_jar, args=(jar,))
    # t.start()
    # res = subprocess.run(command, check=True, capture_output=True, text=True)
    return t


def start_docker_env():
    print("Setup docker environment...")
    os.chdir("deployment/local-env")
    command = "docker compose up"
    t = subprocess.Popen(command)
    os.chdir("../..")
    print("Docker environment is running")
    return t


def shutdown_docker_env():
    print("Stop docker environment...")
    os.chdir("deployment/local-env")
    command = "docker compose down"
    subprocess.check_output(command, check=True, capture_output=True, text=True)
    os.chdir("../..")
    print("Docker environment has been stopped")


def start_frontend():
    os.chdir("knowhere-vui")
    os.system("npm install -g @vue/cli-service")
    os.system("npm install")
    os.system("npm run dev")
    os.chdir("..")


def stop_all_process(processes):
    for t in processes:
        t.terminate()
        shutdown_docker_env()


def main():
    processes = []

    subp = start_docker_env()
    processes.append(subp)

    services = [
        "api-gateway",
        "auth-service",
        "video-service",
        "enrollment-service",
        "grade-service",
        "notification-service",
        "quiz-service",
        "subject-service",
    ]

    try:
        [processes.append(build_and_start_java_app(app)) for app in services]
    except:
        print("Cannot start all java app. Exit")
        stop_all_process(processes)
        return -1

    start_frontend()
    stop_all_process(processes)

    print("Exit")


main()
