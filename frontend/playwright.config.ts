import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './tests',  // ✅ Your test files directory
  reporter: [
    ['list'],  // ✅ Console output
    ['json', { outputFile: './test-results/results.json' }],  // ✅ Used in summary script
    ['html', { outputFolder: './html-report', open: 'never' }]  // ✅ Moved outside to avoid conflict
  ],
  use: {
    headless: true,
    viewport: { width: 1280, height: 720 },
    actionTimeout: 0,
    baseURL: 'http://localhost:4200',  // ✅ Your Angular app
  },
});
