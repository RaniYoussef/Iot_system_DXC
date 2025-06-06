import { test, expect } from '@playwright/test';

test('TC007 - Filter with date range before data collection started', async ({ page }) => {
  // 1. Open the light dashboard page
  await page.goto('http://localhost:4200/light-dashboard');

  // 2. Set an invalid/past range where no data exists
  await page.locator('input[type="date"]').nth(0).fill('2024-01-20'); // From Date
  await page.locator('input[type="date"]').nth(1).fill('2025-05-24'); // To Date

  // 3. Apply filters
  await page.getByRole('button', { name: 'Apply Filters' }).click();

  // 4. Check if either:
  //    a. Only data from 2025-04-21 appears
  //    b. Or a "no data" message appears (based on your app logic)

  const noDataMessage = page.locator('.no-data-message'); // UPDATE this based on your UI
  if (await noDataMessage.isVisible()) {
    await expect(noDataMessage).toContainText(/no data|no results/i); // If no results shown
  } else {
    const records = page.locator('.record-date'); // UPDATE this selector too
    const count = await records.count();
    for (let i = 0; i < count; i++) {
      const dateText = await records.nth(i).innerText();
      const date = new Date(dateText);
      expect(date >= new Date('2025-04-21')).toBeTruthy();
    }
  }
});
