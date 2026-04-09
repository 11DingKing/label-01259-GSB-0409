<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-brand">
          <div class="brand-logo">
            <svg viewBox="0 0 40 40" fill="none">
              <circle cx="20" cy="20" r="20" fill="url(#footer-gradient)"/>
              <path d="M12 20L18 26L28 14" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
              <defs>
                <linearGradient id="footer-gradient" x1="0" y1="0" x2="40" y2="40">
                  <stop stop-color="#FF6B6B"/>
                  <stop offset="1" stop-color="#FF8E53"/>
                </linearGradient>
              </defs>
            </svg>
            <span>Creatorly</span>
          </div>
          <p class="brand-desc">支持原创创作者，让创作更有价值</p>
        </div>
        
        <div class="footer-links">
          <div class="link-group">
            <h4>平台</h4>
            <router-link to="/">首页</router-link>
            <router-link to="/creators">发现创作者</router-link>
            <router-link to="/apply-creator">成为创作者</router-link>
          </div>
          <div class="link-group">
            <h4>关于</h4>
            <span class="link-text">支持原创创作</span>
            <span class="link-text">连接创作者与粉丝</span>
            <span class="link-text">让创作更有价值</span>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2024 Creatorly. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
.footer {
  background: $gradient-dark;
  color: rgba(white, 0.8);
  padding: $spacing-3xl 0 $spacing-lg;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: 1.5fr 2fr;
  gap: $spacing-3xl;
  padding-bottom: $spacing-2xl;
  border-bottom: 1px solid rgba(white, 0.1);
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: $spacing-xl;
  }
}

.footer-brand {
  .brand-logo {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    margin-bottom: $spacing-md;
    
    svg {
      width: 40px;
      height: 40px;
    }
    
    span {
      font-size: $font-size-xl;
      font-weight: $font-weight-bold;
      color: white;
    }
  }
  
  .brand-desc {
    color: rgba(white, 0.6);
    margin-bottom: $spacing-lg;
    max-width: 280px;
  }
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xl;
  
  @media (max-width: 480px) {
    grid-template-columns: 1fr;
  }
  
  .link-group {
    h4 {
      font-size: $font-size-sm;
      font-weight: $font-weight-semibold;
      color: white;
      text-transform: uppercase;
      letter-spacing: 0.1em;
      margin-bottom: $spacing-md;
    }
    
    a {
      display: block;
      color: rgba(white, 0.6);
      padding: $spacing-xs 0;
      transition: color $transition-fast;
      
      &:hover {
        color: white;
      }
    }
    
    .link-text {
      display: block;
      color: rgba(white, 0.5);
      padding: $spacing-xs 0;
      font-size: $font-size-sm;
    }
  }
}

.footer-bottom {
  padding-top: $spacing-lg;
  text-align: center;
  
  p {
    font-size: $font-size-sm;
    color: rgba(white, 0.4);
  }
}
</style>
