import { test, expect } from '@playwright/test';

test('TC001 - Dashboard shows 3 main cards with correct labels', async ({ page }) => {
  await page.goto('http://localhost:4200/dashboard');

  const cards = page.locator('.dashboard-cards .card');
  await expect(cards).toHaveCount(3);

  await expect(cards.nth(0).locator('h3')).toHaveText(/Traffic Monitoring/i);
  await expect(cards.nth(1).locator('h3')).toHaveText(/Street Light Management/i);
  await expect(cards.nth(2).locator('h3')).toHaveText(/Air Pollution Monitoring/i);
});
