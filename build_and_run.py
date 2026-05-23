#!/usr/bin/env python3
"""
Portfolio Tracker Build and Run Script
Compiles Java source files and launches the JavaFX application
"""
import os
import subprocess
import glob
import sys

def main():
    # Setup paths
    m2_repo = os.path.expanduser('~/.m2/repository')
    project_dir = os.getcwd()
    bin_dir = os.path.join(project_dir, 'bin')
    lib_dir = os.path.join(project_dir, 'lib')
    src_dir = os.path.join(project_dir, 'src')
    
    # Create bin directory
    os.makedirs(bin_dir, exist_ok=True)
    
    # Build compile classpath using bundled libraries
    print("Building classpath...")
    compile_jars = glob.glob(os.path.join(lib_dir, '*.jar'))
    
    if not compile_jars:
        print("ERROR: No JARs found in lib directory!")
        print(f"Searched in: {lib_dir}")
        sys.exit(1)
    
    compile_cp = ';'.join(compile_jars)
    
    # Compile Java files
    java_files = glob.glob(os.path.join(src_dir, '*.java'))
    print(f"Found {len(java_files)} Java files to compile")
    
    print("Compiling...")
    result = subprocess.run(
        ['javac', '-d', bin_dir, '--module-path', lib_dir, '-cp', compile_cp] + java_files,
        capture_output=True,
        text=True
    )
    
    if result.returncode != 0:
        print("❌ Compilation FAILED!")
        print(result.stderr[:3000])
        sys.exit(1)
    
    print("✅ Compilation SUCCESSFUL!")
    
    # Count compiled classes
    class_files = len(glob.glob(os.path.join(bin_dir, '*.class')))
    print(f"   Generated {class_files} class files")
    
    # Build runtime classpath
    print("\nBuilding runtime classpath...")
    lib_jars = glob.glob(os.path.join(lib_dir, '*.jar'))
    runtime_cp = bin_dir + ';' + ';'.join(lib_jars)
    
    print(f"✅ Ready to run!")
    print("\n" + "="*60)
    print("Starting Portfolio Tracker Application")
    print("="*60)
    print("Java Version:")
    subprocess.run(['java', '-version'])
    print("\n🚀 Launching JavaFX application...\n")
    
    # Run the application with JavaFX module path
    result = subprocess.run([
        'java',
        '--module-path', lib_dir,
        '--add-modules', 'javafx.controls,javafx.fxml',
        '--enable-native-access=javafx.graphics',
        '-cp', runtime_cp,
        'PortfolioTrackerApp'
    ])
    sys.exit(result.returncode)

if __name__ == '__main__':
    main()
