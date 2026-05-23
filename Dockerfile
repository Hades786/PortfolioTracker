FROM eclipse-temurin:24-jdk

WORKDIR /app

# Install OS dependencies required by JavaFX on Linux
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgtk-3-0 \
    libgconf-2-4 \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxrandr2 \
    libxi6 \
    libxtst6 \
    libpangocairo-1.0-0 \
    libatk1.0-0 \
    libc6 \
    && rm -rf /var/lib/apt/lists/*

COPY . /app

RUN mkdir -p bin
RUN javac -d bin --module-path lib -cp "lib/*" src/*.java

ENV CLASSPATH=bin:lib/*
ENV DISPLAY=:0

CMD ["java", "--module-path", "lib", "--add-modules", "javafx.controls,javafx.fxml", "--enable-native-access=javafx.graphics", "-cp", "bin:lib/*", "PortfolioTrackerApp"]
