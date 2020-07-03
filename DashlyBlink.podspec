
  Pod::Spec.new do |s|
    s.name = 'DashlyBlink'
    s.version = '0.0.1'
    s.summary = 'Connect over wifi to the Dashly Blink Magnet'
    s.license = 'MIT'
    s.homepage = 'https://github.com/dashly/blink'
    s.author = 'Jack Everitt'
    s.source = { :git => 'https://github.com/dashly/blink', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
    s.dependency 'SwiftSocket'
  end